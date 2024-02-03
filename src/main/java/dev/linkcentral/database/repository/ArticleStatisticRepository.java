package dev.linkcentral.database.repository;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.entity.ArticleStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleStatisticRepository extends JpaRepository<ArticleStatistic, Long> {

    Optional<ArticleStatistic> findByArticle(Article article);
}
