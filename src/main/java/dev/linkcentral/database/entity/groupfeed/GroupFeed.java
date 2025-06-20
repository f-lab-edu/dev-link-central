package dev.linkcentral.database.entity.groupfeed;

import dev.linkcentral.database.entity.AuditingFields;
import dev.member.entity.Member;
import dev.linkcentral.service.dto.groupfeed.GroupFeedCreateDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class GroupFeed extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "writer", length = 50, nullable = false)
    private String writer;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    protected GroupFeed() {
    }


    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public GroupFeed(Member member, String title, String content, String writer, String imagePath) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.imageUrl = imagePath;
    }

    public static GroupFeed of(Member member, GroupFeedCreateDTO groupFeedCreateDTO, String imagePath) {
        return new GroupFeed(
                member,
                groupFeedCreateDTO.getTitle(),
                groupFeedCreateDTO.getContent(),
                groupFeedCreateDTO.getWriter(),
                imagePath);
    }
}
