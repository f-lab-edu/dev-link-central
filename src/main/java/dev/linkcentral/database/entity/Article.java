package dev.linkcentral.database.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
public class Article extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member memberId;

    @Setter
    @Column(length = 100, nullable = false)
    private String title;

    @Setter
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Setter
    @Column(length = 100, nullable = false)
    private String writer;

    protected Article() {
    }
}