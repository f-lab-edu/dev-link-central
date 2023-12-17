package dev.linkcentral.database.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class MemberFeedComment extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_feed_id")
    private MemberFeed memberFeedId;

    @Column(columnDefinition = "TEXT")
    private String content;

    protected MemberFeedComment() {
    }


    public void updateContent(String content) {
        this.content = content;
    }
}