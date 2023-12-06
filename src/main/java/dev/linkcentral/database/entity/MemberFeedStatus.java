package dev.linkcentral.database.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
public class MemberFeedStatus extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_feed_id")
    private MemberFeed MemberFeedId;

    @Setter
    private int likes;

    @Setter
    private int view;

    protected MemberFeedStatus() {
    }
}