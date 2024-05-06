package dev.linkcentral.database.repository;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.entity.ArticleStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleStatisticRepository extends JpaRepository<ArticleStatistic, Long> {

    // Article 엔티티와 연결된 모든 ArticleStatistic 인스턴스를 삭제하는 메서드
    void deleteByArticle(Article article);

    Optional<ArticleStatistic> findByArticle(Article article);
}
