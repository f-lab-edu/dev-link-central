package dev.linkcentral.service.dto.studygroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupDetailsDTO {

    private Long id;
    private String groupName;
    private String studyTopic;
    private boolean leaderStatus;

}
