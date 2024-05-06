package dev.linkcentral.presentation.response.studygroup;

import dev.linkcentral.service.dto.studygroup.StudyGroupDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupDetailsResponse {

    private Long id;
    private String groupName;
    private String studyTopic;
    private boolean leaderStatus;

    public static StudyGroupDetailsResponse toStudyGroupDetailsResponse(StudyGroupDetailsDTO studyGroup) {
        return StudyGroupDetailsResponse.builder()
                .id(studyGroup.getId())
                .groupName(studyGroup.getGroupName())
                .studyTopic(studyGroup.getStudyTopic())
                .leaderStatus(studyGroup.isLeaderStatus())
                .build();
    }
}
