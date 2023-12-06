package dev.linkcentral.database.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
public class Profile extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member memberId;

    @Setter
    @Column(length = 250, nullable = false)
    private String bio;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    protected Profile() {
    }
}