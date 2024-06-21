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

    @GetMapping("/member-info")
    public ResponseEntity<ArticleMemberResponse> getMemberInfo() {
        ArticleCurrentMemberDTO currentMemberId = articleFacade.getCurrentMemberId();
        ArticleMemberResponse response = ArticleMemberResponse.toArticleMemberResponse(currentMemberId.getMemberId());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ArticleCreateResponse> createArticle(@Validated @RequestBody ArticleCreateRequest articleCreateRequest) {
        ArticleCreateRequestDTO createRequestDTO = ArticleCreateRequest.toArticleCreateCommand(articleCreateRequest);
        ArticleCreateDTO articleCreateDTO = articleFacade.createAndSaveArticle(createRequestDTO);
        ArticleCreateResponse response = ArticleCreateResponse.toArticleCreateResponse(articleCreateDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ArticleUpdateResponse> updateArticle(@Validated @RequestBody ArticleUpdateRequest updateRequest) {
        ArticleUpdateRequestDTO updateRequestDTO = ArticleUpdateRequest.toArticleUpdateRequestCommand(updateRequest);
        ArticleUpdatedDTO articleUpdatedDTO = articleFacade.updateArticle(updateRequestDTO);
        ArticleUpdateResponse response = ArticleUpdateResponse.toArticleUpdateResponse(HttpStatus.OK.value(), articleUpdatedDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ArticleDeleteResponse> deleteArticle(@PathVariable Long id) {
        articleFacade.deleteArticle(id);
        ArticleDeleteResponse response = ArticleDeleteResponse.toArticleDeleteResponse();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<ArticleLikeResponse> toggleArticleLike(@PathVariable Long id) {
        ArticleLikeDTO articleLikeDTO = articleFacade.toggleLike(id);
        ArticleLikeResponse response = ArticleLikeResponse.toArticleLikeResponse(articleLikeDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/likes-count")
    public ResponseEntity<ArticleLikesCountResponse> getArticleLikesCount(@PathVariable Long id) {
        ArticleLikesCountDTO likesCountDTO = articleFacade.getLikesCount(id);
        ArticleLikesCountResponse response = ArticleLikesCountResponse.toArticleLikesCountResponse(likesCountDTO.getLikesCount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<ArticleCommentResponse> saveArticleComment(@PathVariable Long id,
                          @Validated @RequestBody ArticleCommentRequest commentRequest) {

        if (commentRequest.getContents() == null) {
            throw new IllegalArgumentException("댓글 내용은 null이 아니어야 합니다.");
        }
        ArticleCommentRequestDTO commentRequestDTO = ArticleCommentRequest.toArticleCommentRequestCommand(commentRequest, id);
        ArticleCommentDTO commentSaveDTO = articleFacade.commentSave(commentRequestDTO);

        ArticleCommentResponse response = ArticleCommentResponse.toCommentResponse(commentSaveDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/comment/{commentId}")
    public ResponseEntity<ArticleCommentUpdateResponse> updateArticleComment(@PathVariable Long commentId,
                          @Validated @RequestBody ArticleCommentRequest commentRequest) {

        ArticleCommentRequestDTO commentRequestDTO = ArticleCommentRequest.toArticleCommentRequestCommand(commentRequest);
        ArticleCommentUpdateDTO commentUpdateDTO = articleFacade.updateComment(commentRequestDTO, commentId);
        ArticleCommentUpdateResponse response = ArticleCommentUpdateResponse.toArticleCommentUpdateResponse(commentUpdateDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteArticleComment(@PathVariable Long commentId) {
        articleFacade.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<ArticleCommentPageResponse> showCommentsForArticle(@PathVariable Long id,
            @RequestParam int offset, @RequestParam int limit) {

        Page<ArticleCommentViewDTO> commentsPage = articleFacade.getCommentsForArticle(id, offset, limit);
        ArticleCommentPageResponse response = ArticleCommentPageResponse.toArticleCommentPageResponse(commentsPage);
        return ResponseEntity.ok(response);
    }
}
