package dev.linkcentral.service;

import dev.linkcentral.common.exception.CustomOptimisticLockException;
import dev.linkcentral.database.entity.*;
import dev.linkcentral.database.repository.*;
import dev.linkcentral.presentation.dto.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleService {

    private static int RETRY_COUNT = 0;
    private static final int MAX_RETRIES = 3; // 최대 재시도 횟수를 정의

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
        return articleMapper.toArticleDetailsDTO(article);
    }

    @Transactional(readOnly = true)
    public List<ArticleViewDTO> findAllArticles() {
        List<Article> articleEntityList = articleRepository.findAll();
        List<ArticleViewDTO> articleDTOList = new ArrayList<>();

        for (Article articleEntity : articleEntityList) {
            ArticleViewDTO articleDTO = articleMapper.toArticleDTO(articleEntity);
            articleDTOList.add(articleDTO);
        }
        return articleDTOList;
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
        int pageLimit = 3;                       // 한 페이지에 보여줄 글 갯수

        // 한페이지당 3개씩 글을 보여주고 정렬 기준은 id 기준으로 내림차순 정렬
        Page<Article> articleEntity = articleRepository.findAll(PageRequest
                .of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        return articleEntity.map(articleMapper::toArticleDTO);
    }

    @Transactional
    public ArticleLikeDTO toggleArticleLike(Long articleId, Member member) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Optional<ArticleLike> like = articleLikeRepository.findByMemberAndArticle(member, article);
        ArticleStatistic articleStatistic = articleStatisticRepository.findByArticle(article)
                .orElseGet(() -> new ArticleStatistic(article));

        boolean isLiked = isLiked(member, article, like, articleStatistic);
        articleStatisticRepository.save(articleStatistic);
        return new ArticleLikeDTO(isLiked, articleStatistic.getLikes());
    }

    private boolean isLiked(Member member, Article article,
                            Optional<ArticleLike> like, ArticleStatistic articleStatistic) {
        boolean isLiked;
        if (like.isPresent()) {
            articleLikeRepository.delete(like.get());
            articleStatistic.decrementLikes();
            isLiked = false;
        } else {
            ArticleLike newLike = new ArticleLike(member, article);
            articleLikeRepository.save(newLike);
            articleStatistic.incrementLikes();
            isLiked = true;
        }
        return isLiked;
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
