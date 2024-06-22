package dev.linkcentral.database.entity.article;

import dev.linkcentral.database.entity.AuditingFields;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class ArticleStatistic extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(name = "likes")
    private int likes;

    @Column(name = "views")
    private int views;

    @Version
    @Column(name = "version")
    private Long version; // 동시성 이슈를 방지하기 위해 낙관적 락

    protected ArticleStatistic() {
    }


    public static ArticleStatistic createEmptyStatistic() {
        return new ArticleStatistic();
    }

    public ArticleStatistic(Article article) {
        this.article = article;
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
