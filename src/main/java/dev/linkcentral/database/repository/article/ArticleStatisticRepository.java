package dev.linkcentral.database.repository.article;

import dev.linkcentral.database.entity.article.Article;
import dev.linkcentral.database.entity.article.ArticleStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleStatisticRepository extends JpaRepository<ArticleStatistic, Long> {

    /**
     * Article 엔티티에 대한 통계 데이터를 삭제
     *
     * @param article 삭제할 통계 데이터를 가진 Article 엔티티
     */
    void deleteByArticle(Article article);

    /**
     * Article 엔티티에 대한 통계 데이터를 조회
     *
     * @param article 조회할 통계 데이터를 가진 Article 엔티티
     * @return 주어진 Article 엔티티에 대한 통계 데이터를 가진 Optional 객체
     */
    Optional<ArticleStatistic> findByArticle(Article article);
}
