package dev.linkcentral.database.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class ArticleStatus extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article articleId;

    private int likes;

    private int view;

    protected ArticleStatus() {
    }


    public void updateLikes(int likes) {
        this.likes = likes;
    }

    public void updateView(int view) {
        this.view = view;
    }
}