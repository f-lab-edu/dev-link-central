package dev.linkcentral.database.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class MemberFeedStatistic extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_feed_id")
    private MemberFeed memberFeed;

    private int likes;

    private int views;

    protected MemberFeedStatistic() {
    }


    public void updateLikes(int like) {
        this.likes = like;
    }

    public void updateViews(int view) {
        this.views = view;
    }
}