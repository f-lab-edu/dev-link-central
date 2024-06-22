package dev.linkcentral.database.repository.article;

import dev.linkcentral.database.entity.article.Article;
import dev.linkcentral.database.entity.article.ArticleLike;
import dev.linkcentral.database.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {

    void deleteByArticle(Article article);

    long countByArticle(Article article);

    Optional<ArticleLike> findByMemberAndArticle(Member member, Article article);
}