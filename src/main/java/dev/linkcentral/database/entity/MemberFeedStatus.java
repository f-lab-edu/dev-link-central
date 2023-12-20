package dev.linkcentral.database.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class MemberFeedStatus extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_feed_id")
    private MemberFeed memberFeed;

    private int likes;

    private int view;

    protected MemberFeedStatus() {
    }


    public void updateLikes(int likes) {
        this.likes = likes;
    }

    public void updateView(int view) {
        this.view = view;
    }
}