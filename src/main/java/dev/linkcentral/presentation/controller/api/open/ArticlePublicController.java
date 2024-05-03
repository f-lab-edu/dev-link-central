package dev.linkcentral.presentation.controller.api.open;

import dev.linkcentral.presentation.response.article.ArticleDetailsResponse;
import dev.linkcentral.service.dto.article.ArticleDetailsDTO;
import dev.linkcentral.service.facade.ArticleFacade;
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

    private final ArticleFacade articleFacade;

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDetailsResponse> getArticleDetails(@PathVariable Long id) {
        ArticleDetailsDTO articleDetailsDTO = articleFacade.getArticleDetails(id);
        ArticleDetailsResponse response = ArticleDetailsResponse.toArticleDetailsResponse(articleDetailsDTO);
        return ResponseEntity.ok(response);
    }

}
