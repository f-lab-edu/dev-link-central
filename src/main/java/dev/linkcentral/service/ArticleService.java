package dev.linkcentral.service;

import dev.linkcentral.common.exception.ArticleNotFoundException;
import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.repository.ArticleRepository;
import dev.linkcentral.service.dto.request.ArticleRequestDTO;
import dev.linkcentral.service.dto.request.ArticleUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
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
        return articleRepository.findById(id)
                .map(ArticleRequestDTO::toArticleDTO)
                .orElseThrow(() -> new ArticleNotFoundException());
    }

    public ArticleRequestDTO update(ArticleUpdateRequestDTO articleDTO) {
        Article articleEntity = Article.toUpdateEntity(articleDTO);
        Article updateArticle = articleRepository.save(articleEntity);
        return findById(updateArticle.getId());
    }
}