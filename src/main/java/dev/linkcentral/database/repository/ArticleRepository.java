package dev.linkcentral.database.repository;

import dev.linkcentral.database.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {

}
