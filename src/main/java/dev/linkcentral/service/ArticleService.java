package dev.linkcentral.service;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.repository.ArticleRepository;
import dev.linkcentral.service.dto.request.ArticleRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public void save(ArticleRequestDTO articleDTO) {
        Article articleEntity = Article.toSaveEntity(articleDTO);
        articleRepository.save(articleEntity);
    }
}
