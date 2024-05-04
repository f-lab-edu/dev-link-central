package dev.linkcentral.service.dto.studygroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyGroupJoinRequestDTO {

    private Long id;
    private String memberName;
    private String groupName;

}
