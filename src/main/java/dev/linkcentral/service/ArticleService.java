package dev.linkcentral.service;

import dev.linkcentral.common.exception.CustomOptimisticLockException;
import dev.linkcentral.common.exception.ResourceNotFoundException;
import dev.linkcentral.database.entity.article.*;
import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.repository.article.*;
import dev.linkcentral.database.repository.member.MemberRepository;
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

    private final ArticleRepository articleRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleStatisticRepository articleStatisticRepository;
    private final ArticleViewRepository articleViewRepository;
    private final MemberRepository memberRepository;
    private final ArticleMapper articleMapper;
    private final ArticleCommentMapper articleCommentMapper;

    /**
     * 새로운 게시글을 저장합니다.
     *
     * @param articleDTO 저장할 기사의 정보
     * @return 저장된 기사의 정보
     */
    @Transactional
    public ArticleCreateDTO saveArticle(ArticleCreateDTO articleDTO) {
        Member member = findMemberById(articleDTO.getWriterId());
        Article article = articleMapper.toArticleEntity(articleDTO, member);
        Article savedArticle = articleRepository.save(article);
        return articleMapper.toArticleCreateDTO(savedArticle);
    }

    /**
     * ID로 게시글의 상세 정보를 가져옵니다.
     *
     * @param id 기사의 ID
     * @return 기사의 상세 정보
     */
    @Transactional(readOnly = true)
    public ArticleDetailsDTO getArticleById(Long id) {
        Article article = getArticleDetails(id);
        return articleMapper.toArticleDetailsDTO(article);
    }

    /**
     * ID로 게시글을 조회하고 조회 수를 업데이트합니다.
     *
     * @param id 기사의 ID
     * @param member 조회한 회원의 정보
     * @return 조회된 기사의 정보와 조회 수
     */
    @Transactional
    public ArticleViewDTO findArticleById(Long id, Member member) {
        Article article = findArticleById(id);
        viewCountUpdate(member, article);
        ArticleStatistic statistic = getArticleStatistic(article);
        return articleMapper.toDetailedArticleDTO(article, statistic.getViews());
    }

    /**
     * 게시글을 업데이트합니다.
     *
     * @param articleDTO 업데이트할 기사의 정보
     * @return 업데이트된 기사의 정보
     */
    @Transactional
    public ArticleUpdatedDTO updateArticle(ArticleUpdateDTO articleDTO) {
        Article articleEntity = articleMapper.toUpdateEntity(articleDTO);
        Article updatedArticle = articleRepository.save(articleEntity);
        return findUpdatedArticleDTO(updatedArticle.getId());
    }

    /**
     * 게시글을 삭제합니다.
     *
     * @param id 삭제할 기사의 ID
     */
    @Transactional
    public void deleteArticle(Long id) {
        Article article = findArticleById(id);
        deleteAllRelatedEntities(article);
        articleRepository.delete(article);
    }

    /**
     * 페이지네이션을 사용하여 게시글 목록을 가져옵니다.
     *
     * @param pageable 페이지 정보
     * @return 페이지네이션된 기사 목록
     */
    @Transactional(readOnly = true)
    public Page<ArticleViewDTO> paginateArticles(Pageable pageable) {
        Page<Article> articleEntity = articleRepository.findAll(getPageRequest(pageable));
        return articleEntity.map(articleMapper::toArticleDTO);
    }

    /**
     * 게시글의 좋아요 상태를 토글합니다.
     *
     * @param articleId 기사의 ID
     * @param member 좋아요를 토글한 회원의 정보
     * @return 토글된 좋아요 상태와 좋아요 수
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
     * 게시글의 좋아요 수를 가져옵니다.
     *
     * @param articleId 기사의 ID
     * @return 기사의 좋아요 수
     */
    @Transactional(readOnly = true)
    public ArticleLikesCountDTO countArticleLikes(Long articleId) {
        Article article = findArticleById(articleId);
        int likesCount = (int) articleLikeRepository.countByArticle(article);
        return new ArticleLikesCountDTO(likesCount);
    }

    /**
     * 게시글에 댓글을 저장합니다.
     *
     * @param commentDTO 저장할 댓글의 정보
     * @param writerNickname 댓글 작성자의 닉네임
     * @return 저장된 댓글의 정보
     */
    @Transactional
    public ArticleCommentDTO saveComment(ArticleCommentDTO commentDTO, String writerNickname) {
        Member member = findMemberByNickname(writerNickname);
        Article article = findArticleById(commentDTO.getArticleId());
        ArticleComment commentEntity = articleCommentMapper.toArticleCommentEntity(commentDTO, writerNickname, article, member);
        ArticleComment savedComment = articleCommentRepository.save(commentEntity);
        return articleCommentMapper.toArticleCommentDTO(savedComment);
    }

    /**
     * 게시글의 댓글을 스크롤링하여 가져옵니다.
     *
     * @param articleId 기사의 ID
     * @param offset 페이지의 오프셋
     * @param limit 한 페이지에 표시할 댓글의 수
     * @return 스크롤링된 댓글 목록
     */
    @Transactional(readOnly = true)
    public Page<ArticleCommentViewDTO> findCommentsForScrolling(Long articleId, int offset, int limit) {
        Article article = findArticleById(articleId);
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by("id").descending());
        return articleCommentRepository.findAllByArticleOrderByIdDesc(article, pageable)
                .map(articleMapper::toCommentDTO);
    }

    /**
     * 게시글의 댓글을 업데이트합니다.
     *
     * @param commentDTO 업데이트할 댓글의 정보
     * @param currentNickname 현재 사용자의 닉네임
     * @return 업데이트된 댓글의 정보
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
     * 게시글의 댓글을 삭제합니다.
     *
     * @param commentId 삭제할 댓글의 ID
     * @param currentNickname 현재 사용자의 닉네임
     */
    @Transactional
    public void deleteComment(Long commentId, String currentNickname) {
        ArticleComment comment = findArticleCommentById(commentId);
        validateCommentOwner(comment, currentNickname);
        articleCommentRepository.delete(comment);
    }

    /**
     * ID로 멤버를 찾습니다.
     *
     * @param writerId 멤버의 ID
     * @return 멤버 정보
     */
    private Member findMemberById(Long writerId) {
        return memberRepository.findById(writerId)
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));
    }

    /**
     * ID로 게시글을 찾습니다.
     *
     * @param articleId 게시글의 ID
     * @return 게시글 정보
     */
    private Article findArticleById(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
    }

    /**
     * ID로 댓글을 찾습니다.
     *
     * @param commentId 댓글의 ID
     * @return 댓글 정보
     */
    private ArticleComment findArticleCommentById(Long commentId) {
        return articleCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));
    }

    /**
     * ID로 게시글의 상세 정보를 가져옵니다.
     *
     * @param id 가져올 게시글의 ID
     * @return 게시글의 상세 정보가 포함된 Article 객체
     */
    @Transactional(readOnly = true)
    public Article getArticleDetails(Long id) {
        return articleRepository.findByIdWithMember(id)
                .orElseThrow(() -> new ResourceNotFoundException("기사를 찾을 수 없습니다."));
    }

    /**
     * 닉네임으로 멤버를 찾습니다.
     *
     * @param nickname 멤버의 닉네임
     * @return 멤버 정보
     */
    private Member findMemberByNickname(String nickname) {
        return memberRepository.findByNicknameAndDeletedFalse(nickname)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
    }

    /**
     * 게시글의 통계 정보를 가져옵니다.
     *
     * @param article 게시글 정보
     * @return 통계 정보
     */
    private ArticleStatistic getArticleStatistic(Article article) {
        return articleStatisticRepository.findByArticle(article)
                .orElse(ArticleStatistic.createEmptyStatistic());
    }

    /**
     * 페이지네이션을 위한 PageRequest 객체를 생성합니다.
     *
     * @param pageable 페이지 정보
     * @return PageRequest 객체
     */
    private PageRequest getPageRequest(Pageable pageable) {
        int page = pageable.getPageNumber() - 1; // page 위치에 있는 값은 0부터 시작한다.
        int pageLimit = 7;                       // 한 페이지에 보여줄 글 갯수
        return PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id"));
    }

    /**
     * 업데이트된 게시글의 DTO를 찾습니다.
     *
     * @param updatedArticleId 업데이트된 게시글의 ID
     * @return 업데이트된 게시글의 DTO
     */
    private ArticleUpdatedDTO findUpdatedArticleDTO(Long updatedArticleId) {
        return articleRepository.findById(updatedArticleId)
                .map(articleMapper::updateArticleAndReturnDTO)
                .orElseThrow(() -> new EntityNotFoundException("업데이트 된 게시글을 찾을 수 없습니다"));
    }

    /**
     * 게시글과 관련된 모든 엔티티를 삭제합니다.
     *
     * @param article 게시글 정보
     */
    private void deleteAllRelatedEntities(Article article) {
        articleLikeRepository.deleteByArticle(article);
        articleStatisticRepository.deleteByArticle(article);
        articleViewRepository.deleteByArticle(article);
        articleCommentRepository.deleteByArticle(article);
    }

    /**
     * 댓글 작성자를 검증합니다.
     *
     * @param comment 댓글 정보
     * @param currentNickname 현재 사용자의 닉네임
     */
    private void validateCommentOwner(ArticleComment comment, String currentNickname) {
        if (!comment.getWriterNickname().equals(currentNickname)) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }
    }

    /**
     * 게시글의 조회 수를 업데이트합니다.
     *
     * @param member 조회한 회원의 정보
     * @param article 조회한 게시글 정보
     */
    private void viewCountUpdate(Member member, Article article) {
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
     * 게시글의 좋아요 상태를 토글합니다.
     *
     * @param articleId 게시글의 ID
     * @param member 좋아요를 토글한 회원의 정보
     * @return 토글된 좋아요 상태와 좋아요 수
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
     * 첫 번째 조회인지 확인합니다.
     *
     * @param member 조회한 회원의 정보
     * @param article 조회한 게시글 정보
     * @return 첫 번째 조회 여부
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
     * 게시글의 통계 정보를 생성하거나 가져옵니다.
     *
     * @param article 게시글 정보
     * @return 통계 정보
     */
    private ArticleStatistic getOrCreateStatistic(Article article) {
        return articleStatisticRepository.findByArticle(article)
                .orElseGet(() -> new ArticleStatistic(article));
    }

    /**
     * 게시글의 좋아요 상태를 업데이트합니다.
     *
     * @param member 좋아요를 토글한 회원의 정보
     * @param article 게시글 정보
     * @param statistic 통계 정보
     * @return 좋아요 상태
     */
    private boolean updateLikes(Member member, Article article, ArticleStatistic statistic) {
        Optional<ArticleLike> existingLike = articleLikeRepository.findByMemberAndArticle(member, article);
        if (existingLike.isPresent()) {
            articleLikeRepository.delete(existingLike.get());
            statistic.decrementLikes();
            return false;
        } else {
            articleLikeRepository.save(new ArticleLike(member, article));
            statistic.incrementLikes();
            return true;
        }
    }

    /**
     * 재시도 시도 중 예외를 처리합니다.
     *
     * @param attempt 재시도 횟수
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
