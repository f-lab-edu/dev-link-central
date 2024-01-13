package dev.linkcentral.presentation;

import dev.linkcentral.service.ArticleService;
import dev.linkcentral.service.dto.request.ArticleRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/save")
    public String saveForm() {
        return "/articles/save";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ArticleRequestDTO articleDTO) {
        log.info("info articleSaveDTO={}", articleDTO);
        articleService.save(articleDTO);

        return "/home";
    }

    @GetMapping("/")
    public String findAll(Model model) {
        List<ArticleRequestDTO> articleList = articleService.findAll();
        model.addAttribute("articleList", articleList);
        return "/articles/list";
    }
}