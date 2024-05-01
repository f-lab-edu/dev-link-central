package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.presentation.request.article.ArticleCommentRequest;
import dev.linkcentral.presentation.request.article.ArticleCreateRequest;
import dev.linkcentral.presentation.request.article.ArticleUpdateRequest;
import dev.linkcentral.presentation.response.article.*;
import dev.linkcentral.service.dto.article.*;
import dev.linkcentral.service.facade.ArticleFacade;
import dev.linkcentral.service.mapper.ArticleCommentMapper;
import dev.linkcentral.service.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
public class ArticleController {

    private final ArticleMapper articleMapper;
    private final ArticleFacade articleFacade;
    private final ArticleCommentMapper articleCommentMapper;

    @PostMapping
    public ResponseEntity<ArticleCreateResponse> createArticle(@RequestBody ArticleCreateRequest articleCreateRequest) {
        ArticleCreateRequestDTO createRequestDTO = articleMapper.toArticleCreateCommand(articleCreateRequest);
        ArticleCreateDTO articleCreateDTO = articleFacade.createAndSaveArticle(createRequestDTO);
        ArticleCreateResponse response = articleMapper.toArticleCreateResponse(articleCreateDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ArticleUpdateResponse> updateArticle(@RequestBody ArticleUpdateRequest updateRequest) {
        ArticleUpdateRequestDTO updateRequestDTO = articleMapper.toArticleUpdateRequestDTO(updateRequest);
        ArticleUpdatedDTO articleUpdatedDTO = articleFacade.updateArticle(updateRequestDTO);
        ArticleUpdateResponse response = new ArticleUpdateResponse(HttpStatus.OK.value(), articleUpdatedDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ArticleDeleteResponse> deleteArticle(@PathVariable Long id) {
        articleFacade.deleteArticle(id);
        ArticleDeleteResponse response = articleMapper.toArticleDeleteResponse();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<ArticleLikeResponse> toggleArticleLike(@PathVariable Long id) {
        ArticleLikeDTO articleLikeDTO = articleFacade.toggleLike(id);
        ArticleLikeResponse response = articleMapper.toArticleLikeResponse(articleLikeDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/likes-count")
    public ResponseEntity<ArticleLikesCountResponse> getArticleLikesCount(@PathVariable Long id) {
        ArticleLikesCountDTO likesCountDTO = articleFacade.getLikesCount(id);
        ArticleLikesCountResponse response = new ArticleLikesCountResponse(likesCountDTO.getLikesCount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<ArticleCommentResponse> saveArticleComment(@PathVariable Long id,
                                                              @RequestBody ArticleCommentRequest commentRequest) {
        if (commentRequest.getContents() == null) {
            throw new IllegalArgumentException("댓글 내용은 null이 아니어야 합니다.");
        }
        commentRequest.setArticleId(id);
        ArticleCommentRequestDTO commentRequestDTO = articleMapper.toArticleCommentRequestDTO(commentRequest);
        ArticleCommentDTO commentSaveDTO = articleFacade.commentSave(commentRequestDTO);

        ArticleCommentResponse response = articleCommentMapper.createCommentResponse(commentSaveDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/comment/{commentId}")
    public ResponseEntity<ArticleCommentUpdateResponse> updateArticleComment(@PathVariable Long commentId,
                                                                      @RequestBody ArticleCommentRequest commentRequest) {
        ArticleCommentRequestDTO commentRequestDTO = articleMapper.toArticleCommentRequestDTO(commentRequest);
        ArticleCommentUpdateDTO commentUpdateDTO = articleFacade.updateComment(commentRequestDTO, commentId);
        ArticleCommentUpdateResponse response = articleCommentMapper.toArticleCommentUpdateResponse(commentUpdateDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteArticleComment(@PathVariable Long commentId) {
        articleFacade.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

}
