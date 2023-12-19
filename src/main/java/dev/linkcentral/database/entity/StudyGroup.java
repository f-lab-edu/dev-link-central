package dev.linkcentral.database.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class StudyGroup extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int studyLeaderId;

    @Column(length = 100, nullable = false)
    private String groupName;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String studyTopic;

    protected StudyGroup() {
    }


    public void updateStudyTopic(String studyTopic) {
        this.studyTopic = studyTopic;
    }
}