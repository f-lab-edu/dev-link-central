package dev.linkcentral.database.repository.article;

import dev.linkcentral.database.entity.article.Article;
import dev.linkcentral.database.entity.article.ArticleView;
import dev.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleViewRepository extends JpaRepository<ArticleView, Long> {

    /**
     * Article 엔티티에 대한 모든 조회 데이터를 삭제
     *
     * @param article 삭제할 조회 데이터를 가진 Article 엔티티
     */
    void deleteByArticle(Article article);

    /**
     * 특정 회원(Member)이 특정 게시글(Article)을 조회했는지 여부를 확인
     *
     * @param member 확인할 회원
     * @param article 확인할 게시글
     * @return 조회 여부 (true: 조회함, false: 조회하지 않음)
     */
    boolean existsByMemberAndArticle(Member member, Article article);
}
