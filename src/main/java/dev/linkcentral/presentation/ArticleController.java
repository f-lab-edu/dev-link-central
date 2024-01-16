package dev.linkcentral.presentation;

import dev.linkcentral.service.ArticleService;
import dev.linkcentral.service.dto.request.ArticleRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public String save(@ModelAttribute ArticleRequestDTO articleDTO) {
        log.info("info articleSaveDTO={}", articleDTO);
        articleService.save(articleDTO);
        return "/home";
    }
}
