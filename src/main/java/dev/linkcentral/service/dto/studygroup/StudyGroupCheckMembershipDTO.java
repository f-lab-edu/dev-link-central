package dev.linkcentral.service.dto.studygroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupCheckMembershipDTO {

    private boolean exists;

}
