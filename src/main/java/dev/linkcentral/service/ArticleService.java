package dev.linkcentral.service;

import dev.linkcentral.common.exception.CustomOptimisticLockException;
import dev.linkcentral.database.entity.*;
import dev.linkcentral.database.repository.*;
import dev.linkcentral.presentation.dto.ArticleCreateDTO;
import dev.linkcentral.presentation.dto.request.ArticleCommentRequest;
import dev.linkcentral.presentation.dto.request.ArticleCreateRequest;
import dev.linkcentral.presentation.dto.request.ArticleUpdateRequest;
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

    @Transactional
    public ArticleCreateDTO saveArticle(ArticleCreateDTO articleDTO) {
        Member member = memberRepository.findById(articleDTO.getWriterId())
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));

        Article article = articleMapper.toArticleEntity(articleDTO, member);
        Article savedArticle = articleRepository.save(article);
        return articleMapper.toArticleCreateDTO(savedArticle);
    }

    @Transactional(readOnly = true)
    public Article getArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException());
    }

    @Transactional(readOnly = true)
    public List<ArticleRequest> findAllArticles() {
        List<Article> articleEntityList = articleRepository.findAll();
        List<ArticleRequest> articleDTOList = new ArrayList<>();

        for (Article articleEntity : articleEntityList) {
            ArticleRequest dto = ArticleRequest.toArticleDTO(articleEntity);
            articleDTOList.add(dto);
        }
        return articleDTOList;
    }

    @Transactional
    public ArticleRequest findArticleById(Long id, Member member) {
        return articleRepository.findById(id)
                .map(article -> {
                    viewCountUpdate(member, article);
                    ArticleStatistic statistic = articleStatisticRepository.findByArticle(article)
                            .orElse(new ArticleStatistic());
                    ArticleRequest dto = ArticleRequest.toArticleDTOWithViews(article, statistic.getViews());
                    return dto;
                }).orElse(null);
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
    public ArticleRequest updateArticle(ArticleUpdateRequest articleDTO) {
        Article articleEntity = Article.toUpdateEntity(articleDTO);
        Article updateArticle = articleRepository.save(articleEntity);

        Optional<Article> optionalArticleEntity = articleRepository.findById(updateArticle.getId());
        if (optionalArticleEntity.isPresent()) {
            Article article = optionalArticleEntity.get();
            return ArticleRequest.toArticleDTO(article);
        }
        return null;
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
    public Page<ArticleRequest> paginateArticles(Pageable pageable) {
        int page = pageable.getPageNumber() - 1; // page 위치에 있는 값은 0부터 시작한다.
        int pageLimit = 3;                       // 한 페이지에 보여줄 글 갯수

        // 한페이지당 3개씩 글을 보여주고 정렬 기준은 id 기준으로 내림차순 정렬
        Page<Article> articleEntity = articleRepository.findAll(PageRequest
                .of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        return articleEntity.map(article -> ArticleRequest.toArticleDTO(article));
    }

    @Transactional
    public void toggleArticleLike(Long articleId, Member member) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Optional<ArticleLike> like = articleLikeRepository.findByMemberAndArticle(member, article);
        ArticleStatistic articleStatistic = articleStatisticRepository.findByArticle(article)
                .orElseGet(() -> new ArticleStatistic(article));

        if (like.isPresent()) {
            articleLikeRepository.delete(like.get());
            articleStatistic.decrementLikes();
        } else {
            ArticleLike newLike = new ArticleLike(member, article);
            articleLikeRepository.save(newLike);
            articleStatistic.incrementLikes();
        }
        articleStatisticRepository.save(articleStatistic);
    }

    @Transactional(readOnly = true)
    public int countArticleLikes(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        return (int) articleLikeRepository.countByArticle(article);
    }

    @Transactional
    public Long saveComment(ArticleCommentRequest commentDTO, String writerNickname) {
        Member member = memberRepository.findByNicknameAndDeletedFalse(writerNickname)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Article article = articleRepository.findById(commentDTO.getArticleId())
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        ArticleComment commentEntity = ArticleComment.toSaveEntity(commentDTO, article, writerNickname);
        commentEntity.updateMember(member);
        articleCommentRepository.save(commentEntity);
        return commentEntity.getId();
    }

    @Transactional(readOnly = true)
    public Page<ArticleCommentRequest> findCommentsForScrolling(Long articleId, Pageable pageable) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 게시판을 찾을 수 없습니다."));

        return articleCommentRepository.findAllByArticleOrderByIdDesc(article, pageable)
                .map(ArticleCommentRequest::toCommentDTO);
    }

    @Transactional(readOnly = true)
    public ArticleCommentRequest findCommentById(Long id) {
        return articleCommentRepository.findById(id)
                .map(ArticleCommentRequest::toCommentDTO)
                .orElseThrow(() -> new EntityNotFoundException("ID로 댓글을 찾을 수 없습니다."));
    }

    @Transactional
    public void updateComment(Long commentId, ArticleCommentRequest commentDTO, String currentNickname) {
        ArticleComment comment = articleCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        if (!comment.getWriterNickname().equals(currentNickname)) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }
        comment.updateContent(commentDTO.getContents());
        articleCommentRepository.save(comment);
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