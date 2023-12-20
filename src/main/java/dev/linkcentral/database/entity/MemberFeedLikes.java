package dev.linkcentral.database.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class MemberFeedLikes extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_feed_id")
    private MemberFeed memberFeed;

}