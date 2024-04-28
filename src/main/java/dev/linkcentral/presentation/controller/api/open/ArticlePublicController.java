package dev.linkcentral.presentation.controller.api.open;

import dev.linkcentral.service.dto.ArticleDetailsDTO;
import dev.linkcentral.presentation.response.article.ArticleDetailsResponse;
import dev.linkcentral.service.ArticleService;
import dev.linkcentral.service.mapper.ArticleMapper;
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
    private final ArticleMapper articleMapper;

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDetailsResponse> getArticleDetails(@PathVariable Long id) {
        ArticleDetailsDTO articleDetailsDTO = articleService.getArticleById(id);
        ArticleDetailsResponse response = articleMapper.toArticleDetailsResponse(articleDetailsDTO);
        return ResponseEntity.ok(response);
    }

}
