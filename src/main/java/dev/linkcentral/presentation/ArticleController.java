package dev.linkcentral.presentation;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.service.ArticleService;
import dev.linkcentral.service.dto.request.ArticleSaveRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public String save(@ModelAttribute ArticleSaveRequestDTO articleSaveDTO) {
        log.info("info articleSaveDTO={}", articleSaveDTO);
        articleService.save(articleSaveDTO);

        return "/home";
    }
}
