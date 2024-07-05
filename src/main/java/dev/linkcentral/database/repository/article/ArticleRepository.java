package dev.linkcentral.database.repository.article;

import dev.linkcentral.database.entity.article.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * ID로 Article 엔티티를 조회하며, 연관된 Member 엔티티를 즉시 로드
     *
     * @param id 조회할 Article 엔티티의 ID
     * @return 주어진 ID로 조회된 Article 엔티티와 연관된 Member 엔티티의 Optional 객체
     */
    @Query("select a from Article a join fetch a.member where a.id = :id")
    Optional<Article> findByIdWithMember(@Param("id") Long id);
}
