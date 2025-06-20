package dev.linkcentral.database.entity.article;

import dev.linkcentral.database.entity.AuditingFields;
import dev.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
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

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "writer_nickname", nullable = false)
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
}
