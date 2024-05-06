package dev.linkcentral.service.dto.studygroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupListJoinRequestsDTO {

    private List<StudyGroupJoinRequestDTO> studyMemberRequests;

}
