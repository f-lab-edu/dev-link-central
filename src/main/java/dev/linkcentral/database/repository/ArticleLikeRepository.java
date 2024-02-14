package dev.linkcentral.database.repository;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.entity.ArticleLike;
import dev.linkcentral.database.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {

    long countByArticle(Article article);

    Optional<ArticleLike> findByMemberAndArticle(Member member, Article article);
}