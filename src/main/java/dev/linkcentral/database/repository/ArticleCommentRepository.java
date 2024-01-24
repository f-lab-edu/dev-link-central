package dev.linkcentral.database.repository;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.entity.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {

    List<ArticleComment> findAllByArticleOrderByIdDesc(Article article);
}
