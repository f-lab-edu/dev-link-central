package dev.linkcentral.database.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
public class Alarm extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member memberId;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String message;

    @Setter
    private int targetId;

    @Setter
    @Column(length = 100)
    private String type;

    @Setter
    private int alarmChecked;

    @Setter
    private LocalDateTime createTime;

    protected Alarm() {
    }
}