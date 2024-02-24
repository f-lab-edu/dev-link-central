package dev.linkcentral.database.repository;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.entity.ArticleComment;
import dev.linkcentral.database.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {

    List<ArticleComment> findAllByArticleOrderByIdDesc(Article article);

    // 특정 Article에 대한 모든 댓글을 페이지네이션과 함께 조회
    Page<ArticleComment> findAllByArticleOrderByIdDesc(Article article, Pageable pageable);

    // 특정 Member가 작성한 모든 댓글을 페이지네이션과 함께 조회
    Page<ArticleComment> findAllByMember(Member member, Pageable pageable);
}
