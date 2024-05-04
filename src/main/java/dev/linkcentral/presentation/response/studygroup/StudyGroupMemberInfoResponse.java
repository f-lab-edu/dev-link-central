package dev.linkcentral.presentation.response.studygroup;

import dev.linkcentral.service.dto.studygroup.StudyGroupMemberInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupMemberInfoResponse {

    private Long memberId;

    public static StudyGroupMemberInfoResponse toStudyGroupMemberResponse(StudyGroupMemberInfoDTO memberInfoDTO) {
        return new StudyGroupMemberInfoResponse(memberInfoDTO.getMemberId());
    }
}
