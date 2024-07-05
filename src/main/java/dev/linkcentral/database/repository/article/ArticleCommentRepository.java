package dev.linkcentral.database.repository.article;

import dev.linkcentral.database.entity.article.Article;
import dev.linkcentral.database.entity.article.ArticleComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {

    /**
     * Article 엔티티와 연관된 모든 ArticleComment 엔티티를 삭제
     *
     * @param article 삭제할 ArticleComment 엔티티와 연관된 Article 엔티티
     */
    void deleteByArticle(Article article);

    /**
     * Article 엔티티와 연관된 모든 ArticleComment 엔티티를 페이지네이션과 함께 ID 역순으로 조회
     *
     * @param article  조회할 ArticleComment 엔티티와 연관된 Article 엔티티
     * @param pageable 페이지네이션 정보를 포함한 Pageable 객체
     * @return 페이지네이션과 함께 조회된 ArticleComment 엔티티의 페이지
     */
    Page<ArticleComment> findAllByArticleOrderByIdDesc(Article article, Pageable pageable);
}
