package dev.linkcentral.presentation.response.studygroup;

import dev.linkcentral.service.dto.studygroup.StudyGroupRegistrationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupCreateResponse {

    private Long id;
    private String groupName;
    private String studyTopic;

    public static StudyGroupCreateResponse toStudyGroupCreateResponse(StudyGroupRegistrationDTO registrationDTO) {
        return new StudyGroupCreateResponse(
                registrationDTO.getId(),
                registrationDTO.getGroupName(),
                registrationDTO.getStudyTopic());
    }
}
