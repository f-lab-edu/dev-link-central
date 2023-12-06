package dev.linkcentral.database.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
public class StudyGroup extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private int studyLeaderId;

    @Setter
    @Column(length = 100, nullable = false)
    private String groupName;

    @Setter
    @Column(columnDefinition = "TEXT", nullable = false)
    private String studyTopic;

    protected StudyGroup() {
    }
}