package dev.linkcentral.presentation.controller.api.open;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/article")
public class ArticlePublicController {

    private final ArticleService articleService;

    @GetMapping("/{id}")
    public ResponseEntity<Article> updateArticleDetails(@PathVariable Long id) {
        Article article = articleService.getArticleById(id);
        return ResponseEntity.ok().body(article);
    }
}
