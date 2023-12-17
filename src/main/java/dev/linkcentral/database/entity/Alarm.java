package dev.linkcentral.database.entity;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
public class Alarm extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member memberId;

    @Column(columnDefinition = "TEXT")
    private String message;

    private int targetId;

    @Column(length = 100)
    private String type;

    private int alarmChecked;

    private LocalDateTime createTime;

    protected Alarm() {
    }

}