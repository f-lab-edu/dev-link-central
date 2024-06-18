package dev.linkcentral.database.repository;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.entity.ArticleComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {

    void deleteByArticle(Article article);

    // 특정 Article에 대한 모든 댓글을 페이지네이션과 함께 조회
    Page<ArticleComment> findAllByArticleOrderByIdDesc(Article article, Pageable pageable);
}
