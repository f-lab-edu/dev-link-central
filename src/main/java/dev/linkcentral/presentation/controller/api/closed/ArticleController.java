package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.presentation.dto.*;
import dev.linkcentral.presentation.dto.request.article.ArticleCommentRequest;
import dev.linkcentral.presentation.dto.request.article.ArticleCreateRequest;
import dev.linkcentral.presentation.dto.request.article.ArticleUpdateRequest;
import dev.linkcentral.presentation.dto.response.article.*;
import dev.linkcentral.service.ArticleService;
import dev.linkcentral.service.MemberService;
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

    private final ArticleService articleService;
    private final MemberService memberService;
    private final ArticleMapper articleMapper;
    private final ArticleCommentMapper articleCommentMapper;

    @PostMapping
    public ResponseEntity<ArticleCreateResponse> save(@RequestBody ArticleCreateRequest articleCreateRequest) {
        Member currentMember = memberService.getCurrentMember();
        ArticleCreateDTO articleCreateDTO = articleMapper.toArticleCreateDTO(articleCreateRequest, currentMember);
        ArticleCreateDTO savedArticleDTO = articleService.saveArticle(articleCreateDTO);
        ArticleCreateResponse response = articleMapper.toArticleCreateResponse(savedArticleDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ArticleUpdateResponse> update(@RequestBody ArticleUpdateRequest updateRequest) {
        Member member = memberService.getCurrentMember();
        ArticleUpdateDTO articleDTO = articleMapper.toArticleUpdateDTO(updateRequest);
        ArticleUpdatedDTO updatedArticleDTO = articleService.updateArticle(articleDTO);
        ArticleUpdateResponse response = new ArticleUpdateResponse(HttpStatus.OK.value(), updatedArticleDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ArticleDeleteResponse> delete(@PathVariable Long id) {
        articleService.deleteArticle(id);
        ArticleDeleteResponse response = articleMapper.toArticleDeleteResponse(true, "성공적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<ArticleLikeResponse> toggleLike(@PathVariable Long id) {
        Member member = memberService.getCurrentMember();
        ArticleLikeDTO likeDTO = articleService.toggleArticleLike(id, member);
        ArticleLikeResponse response = articleMapper.toArticleLikeResponse(likeDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/likes-count")
    public ResponseEntity<ArticleLikesCountResponse> getLikesCount(@PathVariable Long id) {
        Member member = memberService.getCurrentMember();
        ArticleLikesCountDTO likesCountDTO = articleService.countArticleLikes(id);
        ArticleLikesCountResponse response = new ArticleLikesCountResponse(likesCountDTO.getLikesCount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<ArticleCommentResponse> commentSave(@PathVariable Long id,
                                                              @RequestBody ArticleCommentRequest commentRequest) {
        if (commentRequest.getContents() == null) {
            throw new IllegalArgumentException("댓글 내용은 null이 아니어야 합니다.");
        }
        commentRequest.setArticleId(id);
        Member member = memberService.getCurrentMember();
        ArticleCommentDTO commentDTO = articleCommentMapper.toArticleCommentDTO(commentRequest, member.getNickname());
        ArticleCommentDTO saveCommentDTO = articleService.saveComment(commentDTO, member.getNickname());
        ArticleCommentResponse response = articleCommentMapper.createCommentResponse(saveCommentDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/comment/{commentId}")
    public ResponseEntity<ArticleCommentUpdateResponse> updateComment(@PathVariable Long commentId,
                                                                      @RequestBody ArticleCommentRequest commentRequest) {
        Member member = memberService.getCurrentMember();
        ArticleCommentUpdateDTO commentUpdateDTO = articleCommentMapper.toArticleCommentUpdateDto(commentRequest, commentId);
        ArticleCommentUpdateDTO updatedCommentDTO = articleService.updateComment(commentUpdateDTO, member.getNickname());
        ArticleCommentUpdateResponse response = articleCommentMapper.toArticleCommentUpdateResponse(updatedCommentDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        Member member = memberService.getCurrentMember();
        articleService.deleteComment(commentId, member.getNickname());
        return ResponseEntity.ok().build();
    }

}
