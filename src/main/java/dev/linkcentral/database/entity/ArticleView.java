package dev.linkcentral.database.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class ArticleView extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    protected ArticleView() {
    }


    public ArticleView(Member member, Article article) {
        this.member = member;
        this.article = article;
    }
}
