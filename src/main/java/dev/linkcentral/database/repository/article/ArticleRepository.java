package dev.linkcentral.database.repository.article;

import dev.linkcentral.database.entity.article.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {

}
