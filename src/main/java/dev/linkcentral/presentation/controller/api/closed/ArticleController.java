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
     * 현재 사용자의 정보를 가져옵니다.
     *
     * @return 현재 사용자의 ID를 포함하는 ArticleMemberResponse를 반환합니다.
     */
    @GetMapping("/member-info")
    public ResponseEntity<ArticleMemberInfoResponse> getMemberInfo() {
        ArticleCurrentMemberDTO currentMemberId = articleFacade.getCurrentMemberId();
        ArticleMemberInfoResponse response = ArticleMemberInfoResponse.toArticleMemberResponse(currentMemberId.getMemberId());
        return ResponseEntity.ok(response);
    }

    /**
     * 새로운 게시글을 생성합니다.
     *
     * @param articleCreateRequest 생성할 기사의 정보를 포함하는 요청 객체
     * @return 생성된 기사의 정보를 포함하는 ArticleCreateResponse를 반환합니다.
     */
    @PostMapping
    public ResponseEntity<ArticleCreatedResponse> createArticle(@Validated @RequestBody ArticleCreateRequest articleCreateRequest) {
        ArticleCreateRequestDTO createRequestDTO = ArticleCreateRequest.toArticleCreateCommand(articleCreateRequest);
        ArticleCreateDTO articleCreateDTO = articleFacade.createAndSaveArticle(createRequestDTO);
        ArticleCreatedResponse response = ArticleCreatedResponse.toArticleCreateResponse(articleCreateDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글을 업데이트합니다.
     *
     * @param updateRequest 업데이트할 기사의 정보를 포함하는 요청 객체
     * @return 업데이트된 기사의 정보를 포함하는 ArticleUpdateResponse를 반환합니다.
     */
    @PutMapping
    public ResponseEntity<ArticleUpdatedResponse> updateArticle(@Validated @RequestBody ArticleUpdateRequest updateRequest) {
        ArticleUpdateRequestDTO updateRequestDTO = ArticleUpdateRequest.toArticleUpdateRequestCommand(updateRequest);
        ArticleUpdatedDTO articleUpdatedDTO = articleFacade.updateArticle(updateRequestDTO);
        ArticleUpdatedResponse response = ArticleUpdatedResponse.toArticleUpdateResponse(HttpStatus.OK.value(), articleUpdatedDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글을 삭제합니다.
     *
     * @param id 삭제할 기사의 ID
     * @return 삭제 성공 여부를 나타내는 ArticleDeleteResponse를 반환합니다.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ArticleDeletedResponse> deleteArticle(@PathVariable("id") Long id) {
        articleFacade.deleteArticle(id);
        ArticleDeletedResponse response = ArticleDeletedResponse.toArticleDeleteResponse();
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글의 좋아요 상태를 토글합니다.
     *
     * @param id 좋아요 상태를 토글할 기사의 ID
     * @return 좋아요 상태와 좋아요 수를 포함하는 ArticleLikeResponse를 반환합니다.
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<ArticleLikedResponse> toggleArticleLike(@PathVariable Long id) {
        ArticleLikeDTO articleLikeDTO = articleFacade.toggleLike(id);
        ArticleLikedResponse response = ArticleLikedResponse.toArticleLikeResponse(articleLikeDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글의 좋아요 수를 가져옵니다.
     *
     * @param id 좋아요 수를 가져올 기사의 ID
     * @return 좋아요 수를 포함하는 ArticleLikesCountResponse를 반환합니다.
     */
    @GetMapping("/{id}/likes-count")
    public ResponseEntity<ArticleLikesCountedResponse> getArticleLikesCount(@PathVariable Long id) {
        ArticleLikesCountDTO likesCountDTO = articleFacade.getLikesCount(id);
        ArticleLikesCountedResponse response = ArticleLikesCountedResponse.toArticleLikesCountResponse(likesCountDTO.getLikesCount());
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글에 댓글을 추가합니다.
     *
     * @param id 댓글을 추가할 기사의 ID
     * @param commentRequest 댓글의 내용을 포함하는 요청 객체
     * @return 추가된 댓글의 정보를 포함하는 ArticleCommentResponse를 반환합니다.
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
     * 게시글에 달린 댓글을 업데이트합니다.
     *
     * @param commentId 업데이트할 댓글의 ID
     * @param commentRequest 업데이트할 내용을 포함하는 요청 객체
     * @return 업데이트된 댓글의 정보를 포함하는 ArticleCommentUpdateResponse를 반환합니다.
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
     * 게시글에 달린 댓글을 삭제합니다.
     *
     * @param commentId 삭제할 댓글의 ID
     * @return No Content 상태를 반환합니다.
     */
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteArticleComment(@PathVariable Long commentId) {
        articleFacade.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 게시글에 달린 댓글들을 페이지네이션하여 가져옵니다.
     *
     * @param id 댓글을 가져올 기사의 ID
     * @param offset 페이지의 오프셋
     * @param limit 한 페이지에 표시할 댓글의 수
     * @return 댓글의 페이지를 포함하는 ArticleCommentPageResponse를 반환합니다.
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<ArticleCommentPagedResponse> showCommentsForArticle(@PathVariable Long id,
                                                                              @RequestParam int offset, @RequestParam int limit) {

        Page<ArticleCommentViewDTO> commentsPage = articleFacade.getCommentsForArticle(id, offset, limit);
        ArticleCommentPagedResponse response = ArticleCommentPagedResponse.toArticleCommentPageResponse(commentsPage);
        return ResponseEntity.ok(response);
    }
}
