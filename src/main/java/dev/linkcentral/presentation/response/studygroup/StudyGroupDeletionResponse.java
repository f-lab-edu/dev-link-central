package dev.linkcentral.presentation.response.studygroup;

import dev.linkcentral.service.dto.studygroup.StudyGroupDeleteDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupDeletionResponse {

    private boolean success;
    private String message;

    public static StudyGroupDeletionResponse toStudyGroupDeletionResponse(StudyGroupDeleteDTO dto) {
        return new StudyGroupDeletionResponse(
                dto.isSuccess(),
                dto.getMessage());
    }
}
