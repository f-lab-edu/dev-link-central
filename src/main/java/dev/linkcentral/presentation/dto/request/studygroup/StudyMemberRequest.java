package dev.linkcentral.presentation.dto.request.studygroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyMemberRequest {

    private Long id;
    private String memberName;
    private String groupName;
}
