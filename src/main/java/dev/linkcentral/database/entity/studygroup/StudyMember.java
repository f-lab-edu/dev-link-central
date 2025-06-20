package dev.linkcentral.database.entity.studygroup;

import dev.linkcentral.database.entity.AuditingFields;
import dev.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class StudyMember extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id")
    private StudyGroup studyGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StudyGroupStatus status;

    protected StudyMember() {
    }


    public void updateStudyGroupStatus(StudyGroupStatus status) {
        this.status = status;
    }

    public static StudyMember of(Member member, StudyGroup studyGroup, StudyGroupStatus status) {
        return StudyMember.builder()
                .member(member)
                .studyGroup(studyGroup)
                .status(status)
                .build();
    }
}
