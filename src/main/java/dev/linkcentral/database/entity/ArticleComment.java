package dev.linkcentral.database.entity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
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

    public void updateWriterNickname(String writerNickname) {
        this.writerNickname = writerNickname;
    }

    @Builder
    public ArticleComment(Long id, Article article, Member member, String content, String writerNickname) {
        this.id = id;
        this.article = article;
        this.member = member;
        this.content = content;
        this.writerNickname = writerNickname;
    }
}