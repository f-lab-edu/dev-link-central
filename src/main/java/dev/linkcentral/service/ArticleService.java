package dev.linkcentral.service;

import dev.linkcentral.common.exception.CustomOptimisticLockException;
import dev.linkcentral.database.entity.*;
import dev.linkcentral.database.repository.*;
import dev.linkcentral.service.dto.request.ArticleCommentRequestDTO;
import dev.linkcentral.service.dto.request.ArticleRequestDTO;
import dev.linkcentral.service.dto.request.ArticleUpdateRequestDTO;
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
import javax.servlet.http.HttpSession;
import java.util.*;


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

    @Transactional
    public void saveArticle(ArticleRequestDTO articleDTO) {
        Article articleEntity = Article.toSaveEntity(articleDTO);
        articleRepository.save(articleEntity);
    }

    @Transactional(readOnly = true)
    public List<ArticleRequestDTO> findAllArticles() {
        List<Article> articleEntityList = articleRepository.findAll();
        List<ArticleRequestDTO> articleDTOList = new ArrayList<>();

        for (Article articleEntity : articleEntityList) {
            articleDTOList.add(ArticleRequestDTO.toArticleDTO(articleEntity));
        }
        return articleDTOList;
    }

    @Transactional
    public ArticleRequestDTO findArticleById(Long id, Member member) {
        return articleRepository.findById(id)
                .map(article -> {
                    viewCountUpdate(member, article);
                    ArticleStatistic statistic = articleStatisticRepository.findByArticle(article)
                            .orElse(new ArticleStatistic());
                    return ArticleRequestDTO.toArticleDTOWithViews(article, statistic.getViews());
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
            return false; // 이미 조회한 경우
        }
        ArticleView articleView = new ArticleView(member, article);
        articleViewRepository.save(articleView);
        return true; // 처음 조회한 경우
    }

    @Transactional
    public ArticleRequestDTO updateArticle(ArticleUpdateRequestDTO articleDTO) {
        Article articleEntity = Article.toUpdateEntity(articleDTO);
        Article updateArticle = articleRepository.save(articleEntity);

        Optional<Article> optionalArticleEntity = articleRepository.findById(updateArticle.getId());
        if (optionalArticleEntity.isPresent()) {
            Article article = optionalArticleEntity.get();
            return ArticleRequestDTO.toArticleDTO(article);
        }
        return null;
    }

    @Transactional
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<ArticleRequestDTO> paginateArticles(Pageable pageable) {
        int page = pageable.getPageNumber() - 1; // page 위치에 있는 값은 0부터 시작한다.
        int pageLimit = 3; // 한 페이지에 보여줄 글 갯수

        // 한페이지당 3개씩 글을 보여주고 정렬 기준은 id 기준으로 내림차순 정렬
        Page<Article> articleEntity = articleRepository.findAll(PageRequest
                .of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        return articleEntity.map(article -> new ArticleRequestDTO(
                article.getId(),
                article.getWriter(),
                article.getTitle(),
                article.getCreatedAt()));
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
            articleStatistic.decrementLikes(); // 좋아요 감소
        } else {
            ArticleLike newLike = new ArticleLike(member, article);
            articleLikeRepository.save(newLike);
            articleStatistic.incrementLikes(); // 좋아요 증가
        }
        articleStatisticRepository.save(articleStatistic); // 상태 업데이트 저장
    }

    // 특정 게시글의 "좋아요" 총 갯수를 반환
    @Transactional(readOnly = true)
    public int countArticleLikes(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        return (int) articleLikeRepository.countByArticle(article);
    }

    @Transactional
    public Long saveComment(ArticleCommentRequestDTO commentDTO, String writerNickname) {
        Optional<Article> optionalArticle = articleRepository.findById(commentDTO.getArticleId());
        if (optionalArticle.isPresent()) {
            Article article = optionalArticle.get();
            ArticleComment commentEntity = ArticleComment.toSaveEntity(commentDTO, article, writerNickname);
            articleCommentRepository.save(commentEntity);
            return commentEntity.getId();
        } else {
            throw new EntityNotFoundException("ID가 포함된 게시글을 찾을 수 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public Page<ArticleCommentRequestDTO> findCommentsByMember(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        return articleCommentRepository.findAllByMember(member, pageable)
                .map(ArticleCommentRequestDTO::toCommentDTO);
    }

    @Transactional(readOnly = true)
    public List<ArticleCommentRequestDTO> findAllComments(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        List<ArticleComment> commentList = articleCommentRepository.findAllByArticleOrderByIdDesc(article);

        List<ArticleCommentRequestDTO> commentDTOList = new ArrayList<>();
        for (ArticleComment comment : commentList) {
            commentDTOList.add(ArticleCommentRequestDTO.toCommentDTO(comment)); // 수정된 메서드 호출 방식
        }
        return commentDTOList;
    }


    @Transactional
    public void updateComment(Long commentId, ArticleCommentRequestDTO commentDTO, String currentNickname) {
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

    @Transactional(readOnly = true)
    public Page<ArticleCommentRequestDTO> findCommentsForScrolling(Long articleId, Pageable pageable) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 게시판을 찾을 수 없습니다."));

        return articleCommentRepository.findAllByArticleOrderByIdDesc(article, pageable)
                .map(ArticleCommentRequestDTO::toCommentDTO);
    }


}