package dev.linkcentral.database.repository.article;

import dev.linkcentral.database.entity.article.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("select a from Article a join fetch a.member where a.id = :id")
    Optional<Article> findByIdWithMember(@Param("id") Long id);
}
