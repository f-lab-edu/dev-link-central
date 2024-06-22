package dev.linkcentral.service.helper;

import dev.linkcentral.common.exception.CustomOptimisticLockException;
import dev.linkcentral.database.entity.article.*;
import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.repository.article.*;
import dev.linkcentral.database.repository.member.MemberRepository;
import dev.linkcentral.service.dto.article.ArticleLikeDTO;
import dev.linkcentral.service.dto.article.ArticleUpdatedDTO;
import dev.linkcentral.service.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ArticleServiceHelper {

    public static final int MAX_RETRIES = 3;

    private final ArticleMapper articleMapper;
    private final MemberRepository memberRepository;
    private final ArticleStatisticRepository articleStatisticRepository;
    private final ArticleViewRepository articleViewRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleRepository articleRepository;

    /**
     * ID로 멤버를 찾습니다.
     *
     * @param writerId 멤버의 ID
     * @return 멤버 정보
     */
    public Member findMemberById(Long writerId) {
        return memberRepository.findById(writerId)
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));
    }

    /**
     * ID로 게시글을 찾습니다.
     *
     * @param articleId 게시글의 ID
     * @return 게시글 정보
     */
    public Article findArticleById(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
    }

    /**
     * ID로 댓글을 찾습니다.
     *
     * @param commentId 댓글의 ID
     * @return 댓글 정보
     */
    public ArticleComment findArticleCommentById(Long commentId) {
        return articleCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));
    }

    /**
     * 닉네임으로 멤버를 찾습니다.
     *
     * @param nickname 멤버의 닉네임
     * @return 멤버 정보
     */
    public Member findMemberByNickname(String nickname) {
        return memberRepository.findByNicknameAndDeletedFalse(nickname)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
    }

    /**
     * 게시글의 통계 정보를 가져옵니다.
     *
     * @param article 게시글 정보
     * @return 통계 정보
     */
    public ArticleStatistic getArticleStatistic(Article article) {
        return articleStatisticRepository.findByArticle(article)
                .orElse(ArticleStatistic.createEmptyStatistic());
    }

    /**
     * 페이지네이션을 위한 PageRequest 객체를 생성합니다.
     *
     * @param pageable 페이지 정보
     * @return PageRequest 객체
     */
    public PageRequest getPageRequest(Pageable pageable) {
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
    public ArticleUpdatedDTO findUpdatedArticleDTO(Long updatedArticleId) {
        return articleRepository.findById(updatedArticleId)
                .map(articleMapper::updateArticleAndReturnDTO)
                .orElseThrow(() -> new EntityNotFoundException("업데이트 된 게시글을 찾을 수 없습니다"));
    }

    /**
     * 게시글과 관련된 모든 엔티티를 삭제합니다.
     *
     * @param article 게시글 정보
     */
    public void deleteAllRelatedEntities(Article article) {
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
    public void validateCommentOwner(ArticleComment comment, String currentNickname) {
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
    public void viewCountUpdate(Member member, Article article) {
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
    public ArticleLikeDTO processLikeToggle(Long articleId, Member member) {
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
    public void handleRetry(int attempt) {
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
