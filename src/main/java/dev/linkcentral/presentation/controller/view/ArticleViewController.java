package dev.linkcentral.presentation.controller.view;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.infrastructure.SecurityUtils;
import dev.linkcentral.service.ArticleService;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.presentation.dto.request.ArticleCommentRequest;
import dev.linkcentral.presentation.dto.request.ArticleRequest;
import dev.linkcentral.presentation.dto.response.CommentPageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/view/article")
public class ArticleViewController {

    private final ArticleService articleService;
    private final MemberService memberService;

    @GetMapping("/save-form")
    public String saveForm(Model model) {
        Member currentMember = memberService.getCurrentMember();
        model.addAttribute("nickname", currentMember.getNickname());
        return "articles/save";
    }

    @GetMapping("/")
    public String findAll(Model model) {
        List<ArticleRequest> articleList = articleService.findAllArticles();
        model.addAttribute("articleList", articleList);
        return "/articles/list";
    }

    @GetMapping("/{id}")
    public String findById(@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                           @PathVariable Long id, Model model) {

        Member member = memberService.getAuthenticatedMember();
        ArticleRequest articleDTO = articleService.findArticleById(id, member);

        model.addAttribute("article", articleDTO);
        model.addAttribute("page", pageable.getPageNumber());
        return "/articles/detail";
    }

    @GetMapping("/update-form/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id);
        model.addAttribute("articleUpdate", article);
        return "/articles/update";
    }

    @GetMapping("/delete-form/{id}")
    public String deleteForm(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return "redirect:/api/v1/view/article/";
    }

    @GetMapping("/paging")
    public String paging(@PageableDefault(page = 1) Pageable pageable, Model model) {
        Page<ArticleRequest> articlePage = articleService.paginateArticles(pageable);
        List<ArticleRequest> articleList = articlePage.getContent(); // Page에서 List로 변환

        int blockLimit = 3;
        int startPage = (((int) Math.ceil(((double) pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
        int endPage = Math.min((startPage + blockLimit - 1), articlePage.getTotalPages());

        // 인증된 사용자인지 확인
        boolean isAuthenticated = SecurityUtils.isAuthenticated();
        log.info("--> isAuthenticated: {}", isAuthenticated);

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("articleList", articleList); // List로 전달
        model.addAttribute("articlePage", articlePage); // 페이지 정보 전달
        model.addAttribute("startPage", startPage);     // 시작 페이지
        model.addAttribute("endPage", endPage);         // 마지막 페이지
        return "/articles/paging";
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<CommentPageResponse> getCommentsForArticle(@PathVariable Long id,
                                                                     @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ArticleCommentRequest> commentsPage = articleService.findCommentsForScrolling(id, pageable);
        CommentPageResponse response = new CommentPageResponse(
                commentsPage.getContent(),
                commentsPage.getNumber(),
                commentsPage.getTotalPages()
        );
        return ResponseEntity.ok(response);
    }
}