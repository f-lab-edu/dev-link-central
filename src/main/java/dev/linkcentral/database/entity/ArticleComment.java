package dev.linkcentral.database.entity;

import dev.linkcentral.service.dto.request.ArticleCommentRequestDTO;
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

    public static ArticleComment toSaveEntity(ArticleCommentRequestDTO commentDTO,
                                              Article article, String writerNickname) {
        ArticleComment articleComment = new ArticleComment();
        articleComment.updateContent(commentDTO.getContents());
        articleComment.updateArticle(article);
        articleComment.writerNickname = writerNickname;
        return articleComment;
    }

}