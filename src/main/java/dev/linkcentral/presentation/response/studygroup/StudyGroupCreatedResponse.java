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
public class StudyGroupCreatedResponse {

    private Long id;
    private String groupName;
    private String studyTopic;

    public static StudyGroupCreatedResponse toStudyGroupCreateResponse(StudyGroupRegistrationDTO registrationDTO) {
        return new StudyGroupCreatedResponse(
                registrationDTO.getId(),
                registrationDTO.getGroupName(),
                registrationDTO.getStudyTopic());
    }
}
