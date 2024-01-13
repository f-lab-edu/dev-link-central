package dev.linkcentral.service;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.repository.ArticleRepository;
import dev.linkcentral.service.dto.request.ArticleRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public void save(ArticleRequestDTO articleDTO) {
        Article articleEntity = Article.toSaveEntity(articleDTO);
        articleRepository.save(articleEntity);
    }

    public List<ArticleRequestDTO> findAll() {
        List<Article> articleEntityList = articleRepository.findAll();
        List<ArticleRequestDTO> articleDTOList = new ArrayList<>();

        for (Article articleEntity : articleEntityList) {
            articleDTOList.add(ArticleRequestDTO.toArticleDTO(articleEntity));
        }
        return articleDTOList;
    }

    public ArticleRequestDTO findById(Long id) {
        Optional<Article> optionalArticleEntity = articleRepository.findById(id);
        if (optionalArticleEntity.isPresent()) {
            Article articleEntity = optionalArticleEntity.get();
            ArticleRequestDTO articleDTO = ArticleRequestDTO.toArticleDTO(articleEntity);
            return articleDTO;
        }
        return null;
    }
}