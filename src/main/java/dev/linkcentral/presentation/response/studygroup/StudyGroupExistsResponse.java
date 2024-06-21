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

    private StudyGroupExistsDTO studyGroupExistsDTO;

    public static StudyGroupExistsResponse toStudyGroupExistsResponse(StudyGroupExistsDTO studyGroupExistsDTO) {
        return new StudyGroupExistsResponse(studyGroupExistsDTO);
    }
}
