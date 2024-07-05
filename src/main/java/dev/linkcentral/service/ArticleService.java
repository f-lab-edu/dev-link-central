package dev.linkcentral.service;

import dev.linkcentral.common.exception.CustomOptimisticLockException;
import dev.linkcentral.common.exception.ResourceNotFoundException;
import dev.linkcentral.database.entity.article.*;
import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.repository.article.*;
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

    public static final int MAX_RETRIES = 3;

    private final ArticleMapper articleMapper;
    private final ArticleRepository articleRepository;
    private final ArticleCommentMapper articleCommentMapper;
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleViewRepository articleViewRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleStatisticRepository articleStatisticRepository;

    /**
     * 게시글을 저장
     *
     * @param articleDTO 게시글 생성 DTO
     * @param member     현재 사용자
     * @return 저장된 게시글 DTO
     */
    @Transactional
    public ArticleCreateDTO saveArticle(ArticleCreateDTO articleDTO, Member member) {
        Article article = articleMapper.toArticleEntity(articleDTO, member);
        Article savedArticle = articleRepository.save(article);
        return articleMapper.toArticleCreateDTO(savedArticle);
    }

    /**
     * 게시글을 ID로 조회
     *
     * @param id 게시글 ID
     * @return 게시글 상세 DTO
     */
    @Transactional(readOnly = true)
    public ArticleDetailsDTO getArticleById(Long id) {
        Article article = getArticleDetails(id);
        return articleMapper.toArticleDetailsDTO(article);
    }

    /**
     * 게시글을 ID로 조회하고 조회수를 업데이트
     *
     * @param id     게시글 ID
     * @param member 현재 사용자
     * @return 조회된 게시글 DTO
     */
    @Transactional
    public ArticleViewDTO findArticleById(Long id, Member member) {
        Article article = findArticleById(id);
        viewCountUpdate(member, article);
        ArticleStatistic statistic = getArticleStatistic(article);
        return articleMapper.toDetailedArticleDTO(article, statistic.getViews());
    }

    /**
     * 게시글을 업데이트
     *
     * @param articleDTO 게시글 업데이트 DTO
     * @return 업데이트된 게시글 DTO
     */
    @Transactional
    public ArticleUpdatedDTO updateArticle(ArticleUpdateDTO articleDTO) {
        Article articleEntity = articleMapper.toUpdateEntity(articleDTO);
        Article updatedArticle = articleRepository.save(articleEntity);
        return findUpdatedArticleDTO(updatedArticle.getId());
    }

    /**
     * 게시글을 삭제
     *
     * @param id 게시글 ID
     */
    @Transactional
    public void deleteArticle(Long id) {
        Article article = findArticleById(id);
        deleteAllRelatedEntities(article);
        articleRepository.delete(article);
    }

    /**
     * 페이지네이션을 사용하여 게시글 목록을 조회
     *
     * @param pageable 페이지 요청 정보
     * @return 페이지네이션된 게시글 목록
     */
    @Transactional(readOnly = true)
    public Page<ArticleViewDTO> paginateArticles(Pageable pageable) {
        Page<Article> articleEntity = articleRepository.findAll(getPageRequest(pageable));
        return articleEntity.map(articleMapper::toArticleDTO);
    }

    /**
     * 게시글 좋아요를 토글
     *
     * @param articleId 게시글 ID
     * @param member    현재 사용자
     * @return 좋아요 상태 DTO
     */
    @Transactional
    public ArticleLikeDTO toggleArticleLike(Long articleId, Member member) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                return processLikeToggle(articleId, member);
            } catch (OptimisticLockException e) {
                handleRetry(attempt);
            }
        }
        throw new CustomOptimisticLockException("최대 재시도 횟수 이후 업데이트에 실패했습니다.");
    }

    /**
     * 게시글의 좋아요 개수를 조회
     *
     * @param articleId 게시글 ID
     * @return 좋아요 개수 DTO
     */
    @Transactional(readOnly = true)
    public ArticleLikesCountDTO countArticleLikes(Long articleId) {
        Article article = findArticleById(articleId);
        int likesCount = (int) articleLikeRepository.countByArticle(article);
        return new ArticleLikesCountDTO(likesCount);
    }

    /**
     * 댓글을 저장
     *
     * @param commentDTO 댓글 DTO
     * @param member     현재 사용자
     * @return 저장된 댓글 DTO
     */
    @Transactional
    public ArticleCommentDTO saveComment(ArticleCommentDTO commentDTO, Member member) {
        Article article = findArticleById(commentDTO.getArticleId());
        ArticleComment commentEntity = articleCommentMapper.toArticleCommentEntity(commentDTO, member.getNickname(), article, member);
        ArticleComment savedComment = articleCommentRepository.save(commentEntity);
        return articleCommentMapper.toArticleCommentDTO(savedComment);
    }

    /**
     * 게시글의 댓글 목록을 페이지네이션하여 조회
     *
     * @param articleId 게시글 ID
     * @param offset    페이지 오프셋
     * @param limit     페이지 당 댓글 수
     * @return 페이지네이션된 댓글 목록
     */
    @Transactional(readOnly = true)
    public Page<ArticleCommentViewDTO> findCommentsForScrolling(Long articleId, int offset, int limit) {
        Article article = findArticleById(articleId);
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by("id").descending());
        return articleCommentRepository.findAllByArticleOrderByIdDesc(article, pageable)
                .map(articleMapper::toCommentDTO);
    }

    /**
     * 댓글을 업데이트
     *
     * @param commentDTO      댓글 업데이트 DTO
     * @param currentNickname 현재 사용자 닉네임
     * @return 업데이트된 댓글 DTO
     */
    @Transactional
    public ArticleCommentUpdateDTO updateComment(ArticleCommentUpdateDTO commentDTO, String currentNickname) {
        ArticleComment commentEntity = findArticleCommentById(commentDTO.getId());
        validateCommentOwner(commentEntity, currentNickname);
        commentEntity.updateContent(commentDTO.getContents());
        ArticleComment updatedComment = articleCommentRepository.save(commentEntity);
        return articleCommentMapper.toArticleCommentUpdateDto(updatedComment);
    }

    /**
     * 댓글을 삭제
     *
     * @param commentId       댓글 ID
     * @param currentNickname 현재 사용자 닉네임
     */
    @Transactional
    public void deleteComment(Long commentId, String currentNickname) {
        ArticleComment comment = findArticleCommentById(commentId);
        validateCommentOwner(comment, currentNickname);
        articleCommentRepository.delete(comment);
    }


    /**
     * 주어진 ID로 게시글을 찾는다.
     *
     * @param articleId 게시글 ID
     * @return 게시글 엔티티
     */
    private Article findArticleById(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
    }

    /**
     * 주어진 ID로 댓글을 찾는다.
     *
     * @param commentId 댓글 ID
     * @return 댓글 엔티티
     */
    private ArticleComment findArticleCommentById(Long commentId) {
        return articleCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));
    }

    /**
     * 주어진 ID로 게시글의 상세 정보를 조회
     *
     * @param id 게시글 ID
     * @return 게시글 엔티티
     */
    @Transactional(readOnly = true)
    public Article getArticleDetails(Long id) {
        return articleRepository.findByIdWithMember(id)
                .orElseThrow(() -> new ResourceNotFoundException("기사를 찾을 수 없습니다."));
    }

    /**
     * 주어진 게시글의 통계 정보를 조회
     *
     * @param article 게시글 엔티티
     * @return 게시글 통계 엔티티
     */
    private ArticleStatistic getArticleStatistic(Article article) {
        return articleStatisticRepository.findByArticle(article)
                .orElse(ArticleStatistic.createEmptyStatistic());
    }

    /**
     * 페이지 요청 객체를 생성
     *
     * @param pageable 페이지 요청 정보
     * @return 페이지 요청 객체
     */
    private PageRequest getPageRequest(Pageable pageable) {
        int page = pageable.getPageNumber() - 1; // page 위치에 있는 값은 0부터 시작한다.
        int pageLimit = 7;                       // 한 페이지에 보여줄 글 갯수
        return PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id"));
    }

    /**
     * 업데이트된 게시글 DTO를 조회
     *
     * @param updatedArticleId 업데이트된 게시글 ID
     * @return 업데이트된 게시글 DTO
     */
    private ArticleUpdatedDTO findUpdatedArticleDTO(Long updatedArticleId) {
        return articleRepository.findById(updatedArticleId)
                .map(articleMapper::updateArticleAndReturnDTO)
                .orElseThrow(() -> new EntityNotFoundException("업데이트 된 게시글을 찾을 수 없습니다"));
    }

    /**
     * 주어진 게시글과 관련된 모든 엔티티를 삭제
     *
     * @param article 게시글 엔티티
     */
    private void deleteAllRelatedEntities(Article article) {
        articleLikeRepository.deleteByArticle(article);
        articleStatisticRepository.deleteByArticle(article);
        articleViewRepository.deleteByArticle(article);
        articleCommentRepository.deleteByArticle(article);
    }

    /**
     * 댓글 소유자를 검증
     *
     * @param comment         댓글 엔티티
     * @param currentNickname 현재 사용자 닉네임
     */
    private void validateCommentOwner(ArticleComment comment, String currentNickname) {
        if (!comment.getWriterNickname().equals(currentNickname)) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }
    }

    /**
     * 게시글의 조회수를 업데이트
     *
     * @param member  현재 사용자
     * @param article 게시글 엔티티
     */
    private void viewCountUpdate(Member member, Article article) {
        if (member == null || article == null) {
            return;
        }

        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                if (isFirstView(member, article)) {
                    ArticleStatistic articleStatistic = articleStatisticRepository.findByArticle(article)
                            .orElseGet(() -> new ArticleStatistic(article));
                    articleStatistic.incrementViews();
                    articleStatisticRepository.save(articleStatistic);
                }
                break;
            } catch (OptimisticLockException e) {
                handleRetry(attempt);
            }
        }
    }

    /**
     * 게시글 좋아요 상태를 토글
     *
     * @param articleId 게시글 ID
     * @param member    현재 사용자
     * @return 좋아요 상태 DTO
     */
    private ArticleLikeDTO processLikeToggle(Long articleId, Member member) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        ArticleStatistic statistic = getOrCreateStatistic(article);
        boolean isLiked = updateLikes(member, article, statistic);

        articleStatisticRepository.saveAndFlush(statistic);
        return new ArticleLikeDTO(isLiked, statistic.getLikes());
    }

    /**
     * 사용자가 처음으로 게시글을 조회하는지 확인
     *
     * @param member  현재 사용자
     * @param article 게시글 엔티티
     * @return 처음 조회하는 경우 true, 아니면 false
     */
    private boolean isFirstView(Member member, Article article) {
        boolean alreadyViewed = articleViewRepository.existsByMemberAndArticle(member, article);
        if (alreadyViewed) {
            return false;
        }
        ArticleView articleView = new ArticleView(member, article);
        articleViewRepository.save(articleView);
        return true;
    }

    /**
     * 주어진 게시글의 통계 정보를 조회하거나 생성
     *
     * @param article 게시글 엔티티
     * @return 게시글 통계 엔티티
     */
    private ArticleStatistic getOrCreateStatistic(Article article) {
        return articleStatisticRepository.findByArticle(article)
                .orElseGet(() -> new ArticleStatistic(article));
    }

    /**
     * 게시글 좋아요 상태를 업데이트
     *
     * @param member    현재 사용자
     * @param article   게시글 엔티티
     * @param statistic 게시글 통계 엔티티
     * @return 좋아요 상태
     */
    private boolean updateLikes(Member member, Article article, ArticleStatistic statistic) {
        Optional<ArticleLike> existingLike = articleLikeRepository.findByMemberAndArticle(member, article);
        if (existingLike.isPresent()) {
            articleLikeRepository.delete(existingLike.get());
            statistic.decrementLikes();
            return false;
        }
        articleLikeRepository.save(new ArticleLike(member, article));
        statistic.incrementLikes();
        return true;
    }

    /**
     * 재시도 처리를 수행
     *
     * @param attempt 현재 시도 횟수
     */
    private void handleRetry(int attempt) {
        if (attempt >= MAX_RETRIES - 1) {
            throw new CustomOptimisticLockException("최대 재시도 횟수 이후 업데이트에 실패했습니다.");
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("재시도 중 스레드가 중단되었습니다.", ie);
        }
    }
}
