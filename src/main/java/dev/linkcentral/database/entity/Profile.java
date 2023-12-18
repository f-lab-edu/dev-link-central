package dev.linkcentral.database.entity;

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

    @Column(length = 250, nullable = false)
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    protected Profile() {
    }


    public void updateBio(String bio) {
        this.bio = bio;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}