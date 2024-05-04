package dev.linkcentral.presentation.response.studygroup;

import dev.linkcentral.service.dto.studygroup.StudyGroupCheckMembershipDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupCheckMembershipResponse {

    private boolean exists;

    public static StudyGroupCheckMembershipResponse toStudyGroupCheckMembershipResponse(StudyGroupCheckMembershipDTO membershipDTO) {
        return new StudyGroupCheckMembershipResponse(membershipDTO.isExists());
    }
}
