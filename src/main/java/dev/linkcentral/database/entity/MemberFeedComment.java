package dev.linkcentral.database.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
public class MemberFeedComment extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_feed_id")
    private MemberFeed memberFeedId;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String content;

    protected MemberFeedComment() {
    }
}