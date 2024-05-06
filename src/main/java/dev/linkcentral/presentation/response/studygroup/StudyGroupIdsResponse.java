package dev.linkcentral.presentation.response.studygroup;

import dev.linkcentral.service.dto.studygroup.StudyGroupIdsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupIdsResponse {

    private List<Long> studyGroupIds;

    public static StudyGroupIdsResponse toStudyGroupIdsResponse(StudyGroupIdsDTO studyGroupIdsDTO) {
        return StudyGroupIdsResponse.builder()
                .studyGroupIds(studyGroupIdsDTO.getStudyGroupIds())
                .build();
    }
}
