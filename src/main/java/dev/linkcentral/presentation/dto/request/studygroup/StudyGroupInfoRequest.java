package dev.linkcentral.presentation.dto.request.studygroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyGroupInfoRequest {

    private Long id;
    private String groupName;
    private String studyTopic;
}
