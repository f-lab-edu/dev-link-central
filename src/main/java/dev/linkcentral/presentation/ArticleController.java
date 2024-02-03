package dev.linkcentral.presentation;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.ArticleService;
import dev.linkcentral.service.dto.request.ArticleCommentRequestDTO;
import dev.linkcentral.service.dto.request.ArticleRequestDTO;
import dev.linkcentral.service.dto.request.ArticleUpdateRequestDTO;
import dev.linkcentral.service.dto.response.ArticleEditResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/save")
    public String saveForm() { // TODO(jsp 페이지를 응답하는 controller와 response body 페이지를 응답하는 controller 분리) -> 권한 관리 용이
        return "/articles/save";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ArticleRequestDTO articleDTO, HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        if (member == null) {
            return "/home";
        }

        log.info("info articleSaveDTO={}", articleDTO);
        articleService.save(articleDTO);
        return "redirect:/api/v1/article/paging";
    }

    @GetMapping("/")
    public String findAll(Model model) {
        List<ArticleRequestDTO> articleList = articleService.findAll();
        model.addAttribute("articleList", articleList);
        return "/articles/list";
    }

    @GetMapping("/{id}")
    public String findById(@PageableDefault(page = 1) Pageable pageable,
                           @PathVariable Long id, Model model, HttpSession session) {
        ArticleRequestDTO articleDTO = articleService.findById(id, session);

        // 댓글 목록 가져오기
        List<ArticleCommentRequestDTO> commentDTOList = articleService.commentFindAll(id);
        model.addAttribute("commentList", commentDTOList);

        model.addAttribute("article", articleDTO);
        model.addAttribute("page", pageable.getPageNumber());
        return "/articles/detail";
    }

    @GetMapping("/update/{id}") // TODO(update -> update-form), RESTful API -> url에 리소스를 명시, 행위는 HTTP Method로 구분
    public String updateForm(@PathVariable Long id, Model model, HttpSession session) {
        ArticleRequestDTO articleDTO = articleService.findById(id, session);
        model.addAttribute("articleUpdate", articleDTO);
        return "/articles/update";
    }

    @ResponseBody
    @PutMapping("/update") // TODO(update -> update-form), RESTful API -> url에 리소스를 명시, 행위는 HTTP Method로 구분
    public ArticleEditResponseDTO update(@RequestBody ArticleUpdateRequestDTO articleDTO, Model model) {
        ArticleRequestDTO article = articleService.update(articleDTO);
        model.addAttribute("article", article);
        return new ArticleEditResponseDTO(HttpStatus.OK.value());
    }

    @GetMapping("/delete/{id}") // TODO(update -> update-form), RESTful API -> url에 리소스를 명시, 행위는 HTTP Method로 구분
    public String delete(@PathVariable Long id) {
        articleService.delete(id);
        return "redirect:/api/v1/article/";
    }

    @GetMapping("/paging")
    public String paging(@PageableDefault(page = 1) Pageable pageable, Model model) {
        Page<ArticleRequestDTO> articlePage = articleService.paging(pageable);
        List<ArticleRequestDTO> articleList = articlePage.getContent(); // Page에서 List로 변환

        int blockLimit = 3;
        int startPage = (((int) Math.ceil(((double) pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
        int endPage = Math.min((startPage + blockLimit - 1), articlePage.getTotalPages());

        model.addAttribute("articleList", articleList); // List로 전달
        model.addAttribute("articlePage", articlePage); // 페이지 정보 전달
        model.addAttribute("startPage", startPage);     // 시작 페이지
        model.addAttribute("endPage", endPage);         // 마지막 페이지
        return "/articles/paging";
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long id, HttpSession session) {
        Member member = (Member) session.getAttribute("member");

        // TODO: spring security를 활용(SpringSecurityContextHolder), 공통로직은 별도의 클래스의 메서드로 추출
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        articleService.toggleLike(id, member);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/likes-count")
    public ResponseEntity<Integer> getLikesCount(@PathVariable Long id) {
        int likesCount = articleService.getLikesCount(id);
        return ResponseEntity.ok(likesCount);
    }

    @PostMapping("/comment/save") // TODO(update -> update-form), RESTful API -> url에 리소스를 명시, 행위는 HTTP Method로 구분
    @ResponseBody
    public ResponseEntity<?> commentSave(@RequestBody ArticleCommentRequestDTO commentDTO, HttpSession session) {
        // TODO: spring security를 활용(SpringSecurityContextHolder), 공통로직은 별도의 클래스의 메서드로 추출
        Member member = (Member) session.getAttribute("member");
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        Long saveArticleId = articleService.commentSave(commentDTO, member.getNickname());

        // 작성 성공하면 댓글 목록을 가져와서 반환
        List<ArticleCommentRequestDTO> commentDTOList = articleService.commentFindAll(commentDTO.getArticleId());
        return new ResponseEntity<>(commentDTOList, HttpStatus.OK);
    }

    @ResponseBody
    @PutMapping("/comment/update/{commentId}") // TODO(update -> update-form), RESTful API -> url에 리소스를 명시, 행위는 HTTP Method로 구분
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
    @DeleteMapping("/comment/delete/{commentId}") // TODO(update -> update-form), RESTful API -> url에 리소스를 명시, 행위는 HTTP Method로 구분
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