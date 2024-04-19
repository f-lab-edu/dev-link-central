package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.presentation.dto.*;
import dev.linkcentral.presentation.dto.request.article.ArticleCommentRequest;
import dev.linkcentral.presentation.dto.request.article.ArticleCreateRequest;
import dev.linkcentral.presentation.dto.request.article.ArticleUpdateRequest;
import dev.linkcentral.presentation.dto.response.article.*;
import dev.linkcentral.service.ArticleService;
import dev.linkcentral.service.MemberService;
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
    public ResponseEntity<?> commentSave(@PathVariable Long id, @RequestBody ArticleCommentRequest commentDTO) {
        commentDTO.setArticleId(id);
        if (commentDTO.getContents() == null || commentDTO.getArticleId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("댓글 내용 및 게시판 ID는 null이 아니어야 합니다.");
        }

        Member member = memberService.getCurrentMember();
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("댓글을 달려면 로그인해야 합니다.");
        }

        try {
            Long savedCommentId = articleService.saveComment(commentDTO, member.getNickname());
            if (savedCommentId != null) {
                ArticleCommentRequest savedCommentDTO = articleService.findCommentById(savedCommentId);
                return ResponseEntity.ok(savedCommentDTO);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글을 저장하지 못했습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PutMapping("/comment/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId,
                                           @RequestBody ArticleCommentRequest commentDTO) {
        Member member = memberService.getCurrentMember();
        articleService.updateComment(commentId, commentDTO, member.getNickname());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        Member member = memberService.getCurrentMember();
        articleService.deleteComment(commentId, member.getNickname());
        return ResponseEntity.ok().build();
    }
}
