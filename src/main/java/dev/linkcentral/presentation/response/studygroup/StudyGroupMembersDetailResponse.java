package dev.linkcentral.presentation.response.studygroup;

import dev.linkcentral.service.dto.studygroup.StudyGroupMembersDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupMembersDetailResponse {

    List<StudyGroupMembersDetailDTO> groupMembersDetailDTOS;

    public static StudyGroupMembersDetailResponse toStudyGroupMembersDetailResponse(List<StudyGroupMembersDetailDTO> groupMembersDetailDTOS) {
        return new StudyGroupMembersDetailResponse(groupMembersDetailDTOS);
    }
}
