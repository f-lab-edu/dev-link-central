package dev.linkcentral.database.repository;

import dev.linkcentral.database.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Modifying
    @Query("update Article a set a.writer = :newNickname where a.writer = :oldNickname")
    void updateArticleWriterNickname(@Param("oldNickname") String oldNickname, @Param("newNickname") String newNickname);

}
