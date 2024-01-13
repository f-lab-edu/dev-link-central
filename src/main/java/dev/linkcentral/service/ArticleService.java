package dev.linkcentral.service;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.repository.ArticleRepository;
import dev.linkcentral.service.dto.request.ArticleSaveRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public void save(ArticleSaveRequestDTO articleSaveDTO) {
        Article articleEntity = Article.toSaveEntity(articleSaveDTO);
        articleRepository.save(articleEntity);
    }
}
