package dev.linkcentral.database.repository.article;

import dev.linkcentral.database.entity.article.Article;
import dev.linkcentral.database.entity.article.ArticleLike;
import dev.linkcentral.database.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {

    /**
     * Article 엔티티와 연관된 모든 ArticleLike 엔티티를 삭제
     *
     * @param article 삭제할 ArticleLike 엔티티와 연관된 Article 엔티티
     */
    void deleteByArticle(Article article);

    /**
     * Article 엔티티와 연관된 ArticleLike 엔티티의 수를 반환
     *
     * @param article 조회할 ArticleLike 엔티티와 연관된 Article 엔티티
     * @return Article 엔티티와 연관된 ArticleLike 엔티티의 수
     */
    long countByArticle(Article article);

    /**
     * Member 엔티티와 Article 엔티티로 ArticleLike 엔티티를 조회
     *
     * @param member  조회할 ArticleLike 엔티티와 연관된 Member 엔티티
     * @param article 조회할 ArticleLike 엔티티와 연관된 Article 엔티티
     * @return 주어진 Member 엔티티와 Article 엔티티로 조회된 ArticleLike 엔티티의 Optional 객체
     */
    Optional<ArticleLike> findByMemberAndArticle(Member member, Article article);
}
