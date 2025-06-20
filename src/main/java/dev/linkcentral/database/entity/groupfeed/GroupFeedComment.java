package dev.linkcentral.database.entity.groupfeed;

import dev.linkcentral.database.entity.AuditingFields;
import dev.member.entity.Member;
import dev.linkcentral.service.dto.groupfeed.GroupFeedCommentDTO;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class GroupFeedComment extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_feed_id")
    private GroupFeed groupFeed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "writer_nickname", nullable = false)
    private String writerNickname;

    protected GroupFeedComment() {
    }


    public void updateContent(String content) {
        this.content = content;
    }

    public static GroupFeedComment createComment(GroupFeed groupFeed, Member member, GroupFeedCommentDTO commentDTO) {
        GroupFeedComment comment = new GroupFeedComment();
        comment.groupFeed = groupFeed;
        comment.member = member;
        comment.content = commentDTO.getContent();
        comment.writerNickname = member.getNickname();
        return comment;
    }
}
