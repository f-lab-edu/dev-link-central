package dev.linkcentral.database.entity;

import lombok.Getter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
public class ArticleLike extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    protected ArticleLike() {
    }


    public ArticleLike(Member member, Article article) {
        this.member = member;
        this.article = article;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleLike)) return false;
        ArticleLike that = (ArticleLike) o;
        return Objects.equals(member.getId(), that.member.getId()) &&
                Objects.equals(article.getId(), that.article.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(member.getId(), article.getId());
    }
}
