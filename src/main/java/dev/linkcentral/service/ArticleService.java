package dev.linkcentral.service;

import dev.linkcentral.common.exception.CustomOptimisticLockException;
import dev.linkcentral.database.entity.article.Article;
import dev.linkcentral.database.entity.article.ArticleComment;
import dev.linkcentral.database.entity.article.ArticleStatistic;
import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.repository.article.ArticleCommentRepository;
import dev.linkcentral.database.repository.article.ArticleLikeRepository;
import dev.linkcentral.database.repository.article.ArticleRepository;
import dev.linkcentral.service.dto.article.*;
import dev.linkcentral.service.helper.ArticleServiceHelper;
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

import javax.persistence.OptimisticLockException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleMapper articleMapper;
    private final ArticleCommentMapper articleCommentMapper;
    private final ArticleServiceHelper articleServiceHelper;

    /**
     * 새로운 게시글을 저장합니다.
     *
     * @param articleDTO 저장할 기사의 정보
     * @return 저장된 기사의 정보
     */
    @Transactional
    public ArticleCreateDTO saveArticle(ArticleCreateDTO articleDTO) {
        Member member = articleServiceHelper.findMemberById(articleDTO.getWriterId());
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
        Article article = articleServiceHelper.findArticleById(id);
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
        Article article = articleServiceHelper.findArticleById(id);
        articleServiceHelper.viewCountUpdate(member, article);
        ArticleStatistic statistic = articleServiceHelper.getArticleStatistic(article);
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
        return articleServiceHelper.findUpdatedArticleDTO(updatedArticle.getId());
    }

    /**
     * 게시글을 삭제합니다.
     *
     * @param id 삭제할 기사의 ID
     */
    @Transactional
    public void deleteArticle(Long id) {
        Article article = articleServiceHelper.findArticleById(id);
        articleServiceHelper.deleteAllRelatedEntities(article);
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
        Page<Article> articleEntity = articleRepository.findAll(articleServiceHelper.getPageRequest(pageable));
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
        for (int attempt = 0; attempt < ArticleServiceHelper.MAX_RETRIES; attempt++) {
            try {
                return articleServiceHelper.processLikeToggle(articleId, member);
            } catch (OptimisticLockException e) {
                articleServiceHelper.handleRetry(attempt);
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
        Article article = articleServiceHelper.findArticleById(articleId);
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
        Member member = articleServiceHelper.findMemberByNickname(writerNickname);
        Article article = articleServiceHelper.findArticleById(commentDTO.getArticleId());
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
        Article article = articleServiceHelper.findArticleById(articleId);
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
        ArticleComment commentEntity = articleServiceHelper.findArticleCommentById(commentDTO.getId());
        articleServiceHelper.validateCommentOwner(commentEntity, currentNickname);
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
        ArticleComment comment = articleServiceHelper.findArticleCommentById(commentId);
        articleServiceHelper.validateCommentOwner(comment, currentNickname);
        articleCommentRepository.delete(comment);
    }
}
