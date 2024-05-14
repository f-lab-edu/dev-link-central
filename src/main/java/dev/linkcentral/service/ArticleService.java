package dev.linkcentral.service;

import dev.linkcentral.common.exception.CustomOptimisticLockException;
import dev.linkcentral.common.exception.MemberNotFoundException;
import dev.linkcentral.database.entity.*;
import dev.linkcentral.database.repository.*;
import dev.linkcentral.service.dto.article.*;
import dev.linkcentral.service.mapper.ArticleCommentMapper;
import dev.linkcentral.service.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleService {

    private static int RETRY_COUNT = 0;
    private static final int MAX_RETRIES = 3; // 최대 재시도 횟수

    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;
    private final ArticleStatisticRepository articleStatisticRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleViewRepository articleViewRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleMapper articleMapper;
    private final ArticleCommentMapper articleCommentMapper;

    @Transactional
    public ArticleCreateDTO saveArticle(ArticleCreateDTO articleDTO) {
        Member member = memberRepository.findById(articleDTO.getWriterId())
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));

        Article article = articleMapper.toArticleEntity(articleDTO, member);
        Article savedArticle = articleRepository.save(article);
        return articleMapper.toArticleCreateDTO(savedArticle);
    }

    @Transactional(readOnly = true)
    public ArticleDetailsDTO getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        if (article.getMember() == null) {
            throw new MemberNotFoundException("게시글에 연결된 멤버 정보가 없습니다.");
        }
        return articleMapper.toArticleDetailsDTO(article);
    }

    @Transactional
    public ArticleViewDTO findArticleById(Long id, Member member) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        viewCountUpdate(member, article);
        ArticleStatistic statistic = articleStatisticRepository.findByArticle(article)
                .orElse(new ArticleStatistic());

        return articleMapper.toDetailedArticleDTO(article, statistic.getViews());
    }

    @Transactional
    public void viewCountUpdate(Member member, Article article) {
        while (RETRY_COUNT < MAX_RETRIES) {
            try {
                if (isFirstView(member, article)) {
                    ArticleStatistic articleStatistic = articleStatisticRepository.findByArticle(article)
                            .orElseGet(() -> new ArticleStatistic(article));
                    articleStatistic.incrementViews();
                    articleStatisticRepository.save(articleStatistic);
                }
                break; // 성공적으로 완료시 루프 탈출
            } catch (OptimisticLockException e) {
                RETRY_COUNT++;
                if (RETRY_COUNT >= MAX_RETRIES) {
                    throw new CustomOptimisticLockException("조회수 업데이트를 위한 최대 재시도 횟수 초과");
                }
            }
        }
    }

    private boolean isFirstView(Member member, Article article) {
        boolean alreadyViewed = articleViewRepository.existsByMemberAndArticle(member, article);
        if (alreadyViewed) {
            return false;
        }
        ArticleView articleView = new ArticleView(member, article);
        articleViewRepository.save(articleView);
        return true;
    }

    @Transactional
    public ArticleUpdatedDTO updateArticle(ArticleUpdateDTO articleDTO) {
        Article articleEntity = articleMapper.toUpdateEntity(articleDTO);
        Article updatedArticle = articleRepository.save(articleEntity);

        return articleRepository.findById(updatedArticle.getId())
                .map(articleMapper::updateArticleAndReturnDTO)
                .orElseThrow(() -> new EntityNotFoundException("업데이트 된 기사를 찾을 수 없습니다"));
    }

    @Transactional
    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        articleLikeRepository.deleteByArticle(article);
        articleStatisticRepository.deleteByArticle(article);
        articleViewRepository.deleteByArticle(article);
        articleCommentRepository.deleteByArticle(article);
        articleRepository.delete(article);
    }

    @Transactional(readOnly = true)
    public Page<ArticleViewDTO> paginateArticles(Pageable pageable) {
        int page = pageable.getPageNumber() - 1; // page 위치에 있는 값은 0부터 시작한다.
        int pageLimit = 7;                       // 한 페이지에 보여줄 글 갯수

        // 한페이지당 3개씩 글을 보여주고 정렬 기준은 id 기준으로 내림차순 정렬
        Page<Article> articleEntity = articleRepository.findAll(PageRequest
                .of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        return articleEntity.map(articleMapper::toArticleDTO);
    }

    @Transactional
    public ArticleLikeDTO toggleArticleLike(Long articleId, Member member) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                return processLikeToggle(articleId, member);
            } catch (OptimisticLockException e) {
                handleRetry(attempt, e);
            }
        }
        throw new CustomOptimisticLockException("최대 재시도 횟수 이후 업데이트에 실패했습니다.");
    }

    private ArticleLikeDTO processLikeToggle(Long articleId, Member member) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        ArticleStatistic statistic = getOrCreateStatistic(article);
        boolean isLiked = updateLikes(member, article, statistic);

        articleStatisticRepository.saveAndFlush(statistic);
        return new ArticleLikeDTO(isLiked, statistic.getLikes());
    }

    private ArticleStatistic getOrCreateStatistic(Article article) {
        return articleStatisticRepository.findByArticle(article)
                .orElseGet(() -> new ArticleStatistic(article));
    }

    private boolean updateLikes(Member member, Article article, ArticleStatistic statistic) {
        Optional<ArticleLike> existingLike = articleLikeRepository.findByMemberAndArticle(member, article);
        if (existingLike.isPresent()) {
            // 좋아요가 이미 존재하는 경우, 좋아요를 제거하고 likes 카운트 감소
            articleLikeRepository.delete(existingLike.get());
            statistic.decrementLikes();
            return false;
        } else {
            // 좋아요가 존재하지 않는 경우, 새로운 좋아요를 추가하고 likes 카운트 증가
            articleLikeRepository.save(new ArticleLike(member, article));
            statistic.incrementLikes();
            return true;
        }
    }

    private void handleRetry(int attempt, OptimisticLockException e) {
        if (attempt >= MAX_RETRIES - 1) {
            log.error("좋아요 토글에 도달한 최대 재시도 횟수 입니다: ", e);
            throw e;
        }

        log.info("좋아요 토글 재시도 {}/{}", attempt + 1, MAX_RETRIES);
        try {
            Thread.sleep(100); // 재시도 방지하기 위한 짧은 지연
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("재시도 중 스레드가 중단되었습니다.", ie);
        }
    }

    @Transactional(readOnly = true)
    public ArticleLikesCountDTO countArticleLikes(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        int likesCount = (int) articleLikeRepository.countByArticle(article);
        return new ArticleLikesCountDTO(likesCount);
    }

    @Transactional
    public ArticleCommentDTO saveComment(ArticleCommentDTO commentDTO, String writerNickname) {
        Member member = memberRepository.findByNicknameAndDeletedFalse(writerNickname)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Article article = articleRepository.findById(commentDTO.getArticleId())
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        ArticleComment commentEntity = articleCommentMapper.toArticleCommentEntity(commentDTO, writerNickname, article, member);
        ArticleComment savedComment = articleCommentRepository.save(commentEntity);
        return articleCommentMapper.toArticleCommentDTO(savedComment);
    }

    @Transactional(readOnly = true)
    public Page<ArticleCommentViewDTO> findCommentsForScrolling(Long articleId, Pageable pageable) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 게시판을 찾을 수 없습니다."));

        return articleCommentRepository.findAllByArticleOrderByIdDesc(article, pageable)
                .map(articleMapper::toCommentDTO);
    }

    @Transactional
    public ArticleCommentUpdateDTO updateComment(ArticleCommentUpdateDTO commentDTO, String currentNickname) {
        ArticleComment commentEntity = articleCommentRepository.findById(commentDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        if (!commentEntity.getWriterNickname().equals(currentNickname)) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }
        commentEntity.updateContent(commentDTO.getContents());
        ArticleComment updatedComment = articleCommentRepository.save(commentEntity);
        return articleCommentMapper.toArticleCommentUpdateDto(updatedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, String currentNickname) {
        ArticleComment comment = articleCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        if (!comment.getWriterNickname().equals(currentNickname)) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }
        articleCommentRepository.delete(comment);
    }

}
