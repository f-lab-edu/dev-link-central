package dev.linkcentral.presentation.response.studygroup;

import dev.linkcentral.service.dto.studygroup.StudyGroupJoinRequestDTO;
import dev.linkcentral.service.dto.studygroup.StudyGroupListJoinRequestsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupListJoinResponse {

    private List<StudyGroupJoinRequestDTO> studyMemberRequests;

    public static StudyGroupListJoinResponse toStudyGroupListJoinResponse(StudyGroupListJoinRequestsDTO listJoinRequestsDTO) {
        return StudyGroupListJoinResponse.builder()
                .studyMemberRequests(listJoinRequestsDTO.getStudyMemberRequests())
                .build();
    }
}
