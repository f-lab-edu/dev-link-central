package dev.linkcentral.database.repository;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.entity.ArticleView;
import dev.linkcentral.database.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleViewRepository extends JpaRepository<ArticleView, Long> {

    boolean existsByMemberAndArticle(Member member, Article article);
}
