package dev.linkcentral.presentation.response.studygroup;

import dev.linkcentral.service.dto.studygroup.StudyGroupExistsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupExistsResponse {

    private boolean exists;
    private Long groupId;

    public static StudyGroupExistsResponse toStudyGroupExistsResponse(StudyGroupExistsDTO studyGroupExistsDTO) {
        return StudyGroupExistsResponse.builder()
                .exists(studyGroupExistsDTO.isExists())
                .groupId(studyGroupExistsDTO.getGroupId())
                .build();
    }
}
