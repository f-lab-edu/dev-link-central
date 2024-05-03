package dev.linkcentral.presentation.dto.response;

import dev.linkcentral.presentation.dto.StudyGroupJoinRequestDTO;
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

}
