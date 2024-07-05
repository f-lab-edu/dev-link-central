package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.presentation.request.article.ArticleCommentRequest;
import dev.linkcentral.presentation.request.article.ArticleCreateRequest;
import dev.linkcentral.presentation.request.article.ArticleUpdateRequest;
import dev.linkcentral.presentation.response.article.*;
import dev.linkcentral.service.dto.article.*;
import dev.linkcentral.service.facade.ArticleFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
public class ArticleController {

    private final ArticleFacade articleFacade;

    /**
     * 현재 회원의 정보를 가져옵니다.
     *
     * @return ArticleMemberInfoResponse 현재 회원의 정보 응답
     */
    @GetMapping("/member-info")
    public ResponseEntity<ArticleMemberInfoResponse> getMemberInfo() {
        ArticleCurrentMemberDTO currentMemberId = articleFacade.getCurrentMemberId();
        ArticleMemberInfoResponse response = ArticleMemberInfoResponse.toArticleMemberResponse(currentMemberId.getMemberId());
        return ResponseEntity.ok(response);
    }

    /**
     * 새로운 기사를 생성합니다.
     *
     * @param articleCreateRequest 기사 생성 요청
     * @return ArticleCreatedResponse 생성된 기사 응답
     */
    @PostMapping
    public ResponseEntity<ArticleCreatedResponse> createArticle(@Validated @RequestBody ArticleCreateRequest articleCreateRequest) {
        ArticleCreateRequestDTO createRequestDTO = ArticleCreateRequest.toArticleCreateCommand(articleCreateRequest);
        ArticleCreateDTO articleCreateDTO = articleFacade.createAndSaveArticle(createRequestDTO);
        ArticleCreatedResponse response = ArticleCreatedResponse.toArticleCreateResponse(articleCreateDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 기사를 업데이트합니다.
     *
     * @param updateRequest 기사 업데이트 요청
     * @return ArticleUpdatedResponse 업데이트된 기사 응답
     */
    @PutMapping
    public ResponseEntity<ArticleUpdatedResponse> updateArticle(@Validated @RequestBody ArticleUpdateRequest updateRequest) {
        ArticleUpdateRequestDTO updateRequestDTO = ArticleUpdateRequest.toArticleUpdateRequestCommand(updateRequest);
        ArticleUpdatedDTO articleUpdatedDTO = articleFacade.updateArticle(updateRequestDTO);
        ArticleUpdatedResponse response = ArticleUpdatedResponse.toArticleUpdateResponse(HttpStatus.OK.value(), articleUpdatedDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 기사를 삭제합니다.
     *
     * @param id 기사 ID
     * @return ArticleDeletedResponse 삭제된 기사 응답
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ArticleDeletedResponse> deleteArticle(@PathVariable("id") Long id) {
        articleFacade.deleteArticle(id);
        ArticleDeletedResponse response = ArticleDeletedResponse.toArticleDeleteResponse();
        return ResponseEntity.ok(response);
    }

    /**
     * 기사에 좋아요를 토글합니다.
     *
     * @param id 기사 ID
     * @return ArticleLikedResponse 기사 좋아요 응답
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<ArticleLikedResponse> toggleArticleLike(@PathVariable Long id) {
        ArticleLikeDTO articleLikeDTO = articleFacade.toggleLike(id);
        ArticleLikedResponse response = ArticleLikedResponse.toArticleLikeResponse(articleLikeDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 기사의 좋아요 수를 가져옵니다.
     *
     * @param id 기사 ID
     * @return ArticleLikesCountedResponse 기사 좋아요 수 응답
     */
    @GetMapping("/{id}/likes-count")
    public ResponseEntity<ArticleLikesCountedResponse> getArticleLikesCount(@PathVariable Long id) {
        ArticleLikesCountDTO likesCountDTO = articleFacade.getLikesCount(id);
        ArticleLikesCountedResponse response = ArticleLikesCountedResponse.toArticleLikesCountResponse(likesCountDTO.getLikesCount());
        return ResponseEntity.ok(response);
    }

    /**
     * 기사에 댓글을 추가합니다.
     *
     * @param id 기사 ID
     * @param commentRequest 댓글 요청
     * @return ArticleCommentedResponse 댓글 응답
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<ArticleCommentedResponse> saveArticleComment(@PathVariable Long id,
                                                                       @Validated @RequestBody ArticleCommentRequest commentRequest) {
        if (commentRequest.getContents() == null) {
            throw new IllegalArgumentException("댓글 내용은 null이 아니어야 합니다.");
        }
        ArticleCommentRequestDTO commentRequestDTO = ArticleCommentRequest.toArticleCommentRequestCommand(commentRequest, id);
        ArticleCommentDTO commentSaveDTO = articleFacade.commentSave(commentRequestDTO);

        ArticleCommentedResponse response = ArticleCommentedResponse.toCommentResponse(commentSaveDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 기사 댓글을 업데이트합니다.
     *
     * @param commentId 댓글 ID
     * @param commentRequest 댓글 요청
     * @return ArticleCommentUpdatedResponse 댓글 업데이트 응답
     */
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<ArticleCommentUpdatedResponse> updateArticleComment(@PathVariable Long commentId,
                                                                              @Validated @RequestBody ArticleCommentRequest commentRequest) {
        ArticleCommentRequestDTO commentRequestDTO = ArticleCommentRequest.toArticleCommentRequestCommand(commentRequest);
        ArticleCommentUpdateDTO commentUpdateDTO = articleFacade.updateComment(commentRequestDTO, commentId);
        ArticleCommentUpdatedResponse response = ArticleCommentUpdatedResponse.toArticleCommentUpdateResponse(commentUpdateDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 기사 댓글을 삭제합니다.
     *
     * @param commentId 댓글 ID
     * @return HTTP 204 상태 코드
     */
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteArticleComment(@PathVariable Long commentId) {
        articleFacade.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 기사의 댓글들을 페이지네이션하여 가져옵니다.
     *
     * @param id 기사 ID
     * @param offset 페이지 오프셋
     * @param limit 페이지당 댓글 수
     * @return ArticleCommentPagedResponse 댓글 페이지 응답
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<ArticleCommentPagedResponse> showCommentsForArticle(@PathVariable Long id,
                                                                              @RequestParam int offset, @RequestParam int limit) {
        Page<ArticleCommentViewDTO> commentsPage = articleFacade.getCommentsForArticle(id, offset, limit);
        boolean hasMoreComments = commentsPage.getTotalElements() > (offset + commentsPage.getNumberOfElements());
        ArticleCommentPagedResponse response = ArticleCommentPagedResponse.toArticleCommentPageResponse(commentsPage, hasMoreComments);
        return ResponseEntity.ok(response);
    }
}
