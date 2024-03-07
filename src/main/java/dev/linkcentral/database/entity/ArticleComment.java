package dev.linkcentral.database.entity;

import dev.linkcentral.presentation.dto.request.ArticleCommentRequest;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class ArticleComment extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String writerNickname;

    protected ArticleComment() {
    }


    public void updateContent(String content) {
        this.content = content;
    }

    public void updateArticle(Article article) {
        this.article = article;
    }

    public void updateMember(Member member) {
        this.member = member;
    }

    public static ArticleComment toSaveEntity(ArticleCommentRequest commentDTO, Article article, String writerNickname) {
        ArticleComment articleComment = new ArticleComment();
        articleComment.updateContent(commentDTO.getContents());
        articleComment.updateArticle(article);
        articleComment.writerNickname = writerNickname;
        return articleComment;
    }

    public static ArticleComment create(Article article, Member member, String content) {
        ArticleComment comment = new ArticleComment();
        comment.article = article;
        comment.member = member;
        comment.content = content;
        return comment;
    }
}