package dev.linkcentral.database.entity.groupfeed;

import dev.linkcentral.database.entity.AuditingFields;
import dev.linkcentral.database.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@AllArgsConstructor
public class GroupFeedLike extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_feed_id")
    private GroupFeed groupFeed;

    @Version
    @Column(name = "version")
    private Long version;

    protected GroupFeedLike() {
    }

    public static GroupFeedLike createGroupFeedLike(GroupFeed groupFeed, Member member) {
        GroupFeedLike groupFeedLike = new GroupFeedLike();
        groupFeedLike.updateGroupFeed(groupFeed);
        groupFeedLike.updateMember(member);
        return groupFeedLike;
    }

    public void updateGroupFeed(GroupFeed groupFeed) {
        this.groupFeed = groupFeed;
    }

    public void updateMember(Member member) {
        this.member = member;
    }
}
