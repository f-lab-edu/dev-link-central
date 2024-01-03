package dev.linkcentral.database.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class ArticleStatistic extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    private int likes;

    private int views;

    protected ArticleStatistic() {
    }


    public void updateLikes(int like) {
        this.likes = like;
    }

    public void updateViews(int view) {
        this.views = view;
    }
}