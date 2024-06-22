package dev.linkcentral.database.entity;

import dev.linkcentral.database.entity.member.Member;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
public class Alarm extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "target_member_id")
    private int targetMemberId;

    @Column(name = "type", length = 100)
    private String type;

    @Column(name = "alarm_checked")
    private int alarmChecked;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    protected Alarm() {
    }
}
