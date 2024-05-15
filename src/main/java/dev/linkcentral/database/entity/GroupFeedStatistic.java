package dev.linkcentral.database.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class GroupFeedStatistic extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_feed_id")
    private GroupFeed groupFeed;

    @Column(name = "likes")
    private int likes;

    @Column(name = "views")
    private int views;

    protected GroupFeedStatistic() {
    }


    public void updateLikes(int like) {
        this.likes = like;
    }

    public void updateViews(int view) {
        this.views = view;
    }
}
