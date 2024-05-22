package dev.linkcentral.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class StudyGroup extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "study_leader_id")
    private Long studyLeaderId;

    @Column(name = "group_name", length = 100, nullable = false)
    private String groupName;

    @Column(name = "study_topic", columnDefinition = "TEXT", nullable = false)
    private String studyTopic;

    @Column(name = "is_created")
    private boolean isCreated = false;

    protected StudyGroup() {
    }


    public void updateStudyLeaderId(Long studyLeaderId) {
        this.studyLeaderId = studyLeaderId;
    }

    public void updateGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void updateStudyTopic(String studyTopic) {
        this.studyTopic = studyTopic;
    }

    public void updateCreated(boolean created) {
        isCreated = created;
    }
}
