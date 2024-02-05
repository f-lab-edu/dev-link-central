package dev.linkcentral.presentation;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.ArticleService;
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

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/save")
    public String save(@ModelAttribute ArticleRequestDTO articleDTO, HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        if (member == null) {
            return "/home";
        }
        articleService.saveArticle(articleDTO);
        return "redirect:/api/v1/view/article/paging";
    }

    @PutMapping
    @ResponseBody
    public ArticleEditResponseDTO update(@RequestBody ArticleUpdateRequestDTO articleDTO, Model model) {
        ArticleRequestDTO article = articleService.updateArticle(articleDTO);
        model.addAttribute("article", article);
        return new ArticleEditResponseDTO(HttpStatus.OK.value());
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long id, HttpSession session) {
        Member member = (Member) session.getAttribute("member");

        // TODO: spring security를 활용(SpringSecurityContextHolder), 공통로직은 별도의 클래스의 메서드로 추출
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
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
    public ResponseEntity<?> commentSave(@RequestBody ArticleCommentRequestDTO commentDTO, HttpSession session) {
        // TODO: spring security를 활용(SpringSecurityContextHolder), 공통로직은 별도의 클래스의 메서드로 추출
        Member member = (Member) session.getAttribute("member");
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        Long saveArticleId = articleService.saveComment(commentDTO, member.getNickname());

        // 작성 성공하면 댓글 목록을 가져와서 반환
        List<ArticleCommentRequestDTO> commentDTOList = articleService.findAllComments(commentDTO.getArticleId());
        return new ResponseEntity<>(commentDTOList, HttpStatus.OK);
    }

    @ResponseBody
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId,
                                           @RequestBody ArticleCommentRequestDTO commentDTO,
                                           HttpSession session) {
        // TODO: spring security를 활용(SpringSecurityContextHolder), 공통로직은 별도의 클래스의 메서드로 추출
        Member member = (Member) session.getAttribute("member");
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        articleService.updateComment(commentId, commentDTO, member.getNickname());
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, HttpSession session) {
        // TODO: spring security를 활용(SpringSecurityContextHolder), 공통로직은 별도의 클래스의 메서드로 추출
        Member member = (Member) session.getAttribute("member");
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        articleService.deleteComment(commentId, member.getNickname());
        return ResponseEntity.ok().build();
    }
}