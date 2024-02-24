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

    @Version
    private Long version; // 낙관적 락

    public ArticleStatistic() {
    }

    public ArticleStatistic(Article article) {
        this.article = article;
    }

    public ArticleStatistic(Article article, int likes, int views) {
        this.article = article;
        this.likes = likes;
        this.views = views;
    }

    public void incrementViews() {
        this.views++;
    }

    public void incrementLikes() {
        this.likes++;
    }

    public void decrementLikes() {
        if (this.likes > 0) {
            this.likes--;
        }
    }
}