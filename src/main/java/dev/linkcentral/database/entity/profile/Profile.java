package dev.linkcentral.database.entity.profile;

import dev.linkcentral.database.entity.AuditingFields;
import dev.member.entity.Member;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Profile extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "bio", length = 250, nullable = false)
    private String bio;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    protected Profile() {
    }


    public Profile(Member member, String bio, String imageUrl) {
        this.member = member;
        this.bio = bio;
        this.imageUrl = imageUrl;
    }

    public void updateBio(String bio) {
        this.bio = bio;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
