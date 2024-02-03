package dev.linkcentral.presentation;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.ArticleService;
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
    public String saveForm() {
        return "/articles/save";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ArticleRequestDTO articleDTO, HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        if (member == null) {
            return "/home";
        }

        articleDTO.setWriter(member.getNickname());
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
        Member member = (Member) session.getAttribute("member");
        ArticleRequestDTO articleDTO = articleService.findById(id, member);

        model.addAttribute("article", articleDTO);
        model.addAttribute("page", pageable.getPageNumber());
        return "/articles/detail";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model, HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        ArticleRequestDTO articleDTO = articleService.findById(id, member);
        model.addAttribute("articleUpdate", articleDTO);
        return "/articles/update";
    }

    @ResponseBody
    @PutMapping("/update")
    public ArticleEditResponseDTO update(@RequestBody ArticleUpdateRequestDTO articleDTO, Model model) {
        ArticleRequestDTO article = articleService.update(articleDTO);
        model.addAttribute("article", article);
        return new ArticleEditResponseDTO(HttpStatus.OK.value());
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> delete(@PathVariable Long id) {
        articleService.delete(id);
        return ResponseEntity.ok().body("성공적으로 삭제되었습니다.");
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

}