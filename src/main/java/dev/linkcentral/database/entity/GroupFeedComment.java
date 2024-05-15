package dev.linkcentral.database.entity;

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

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    protected GroupFeedComment() {
    }


    public void updateContent(String content) {
        this.content = content;
    }
}
