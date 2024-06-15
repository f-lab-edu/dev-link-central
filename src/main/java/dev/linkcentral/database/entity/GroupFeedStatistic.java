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

    protected GroupFeedStatistic() {
    }

    public static GroupFeedStatistic createStatistic(GroupFeed groupFeed) {
        GroupFeedStatistic statistic = new GroupFeedStatistic();
        statistic.groupFeed = groupFeed;
        return statistic;
    }

    public void updateLikes(int like) {
        this.likes = like;
    }
}
