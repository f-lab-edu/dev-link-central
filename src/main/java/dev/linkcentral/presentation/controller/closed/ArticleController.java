package dev.linkcentral.presentation.controller.closed;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.ArticleService;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.dto.request.ArticleCommentRequestDTO;
import dev.linkcentral.service.dto.request.ArticleRequestDTO;
import dev.linkcentral.service.dto.request.ArticleUpdateRequestDTO;
import dev.linkcentral.service.dto.response.ArticleEditResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
public class ArticleController {

    private final ArticleService articleService;
    private final MemberService memberService;

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<?> save(@RequestBody ArticleRequestDTO articleDTO) {
        try {
            Member member = memberService.getCurrentMember();
            articleDTO.setWriter(member.getNickname());
            articleService.saveArticle(articleDTO);
            return ResponseEntity.ok("글이 성공적으로 작성되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("글 작성에 실패했습니다.");
        }
    }

    @PutMapping
    @ResponseBody
    public ArticleEditResponseDTO update(@RequestBody ArticleUpdateRequestDTO articleDTO, Model model) {
        ArticleRequestDTO article = articleService.updateArticle(articleDTO);
        model.addAttribute("article", article);
        return new ArticleEditResponseDTO(HttpStatus.OK.value());
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> delete(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.ok().body("성공적으로 삭제되었습니다.");
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long id) {
        Member member = memberService.getCurrentMember();
        articleService.toggleArticleLike(id, member);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/likes-count")
    public ResponseEntity<Integer> getLikesCount(@PathVariable Long id) {
        int likesCount = articleService.countArticleLikes(id);
        return ResponseEntity.ok(likesCount);
    }

    @PostMapping("/comment")
    @ResponseBody
    public ResponseEntity<?> commentSave(@RequestBody ArticleCommentRequestDTO commentDTO) {
        Member member = memberService.getCurrentMember();
        Long saveArticleId = articleService.saveComment(commentDTO, member.getNickname());
        List<ArticleCommentRequestDTO> commentDTOList = articleService.findAllComments(commentDTO.getArticleId());
        return new ResponseEntity<>(commentDTOList, HttpStatus.OK);
    }

    @PutMapping("/comment/{commentId}")
    @ResponseBody
    public ResponseEntity<?> updateComment(@PathVariable Long commentId,
                                           @RequestBody ArticleCommentRequestDTO commentDTO) {
        Member member = memberService.getCurrentMember();
        articleService.updateComment(commentId, commentDTO, member.getNickname());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comment/{commentId}")
    @ResponseBody
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        Member member = memberService.getCurrentMember();
        articleService.deleteComment(commentId, member.getNickname());
        return ResponseEntity.ok().build();
    }
}