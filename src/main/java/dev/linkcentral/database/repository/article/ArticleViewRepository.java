package dev.linkcentral.database.repository.article;

import dev.linkcentral.database.entity.article.Article;
import dev.linkcentral.database.entity.article.ArticleView;
import dev.linkcentral.database.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleViewRepository extends JpaRepository<ArticleView, Long> {

    // Article 엔티티에 연결된 모든 ArticleView 인스턴스를 삭제하는 메서드
    void deleteByArticle(Article article);

    boolean existsByMemberAndArticle(Member member, Article article);
}
