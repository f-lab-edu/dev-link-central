package dev.linkcentral.presentation.dto.request.studygroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyGroupRequest {

    private String groupName;
    private String studyTopic;
}
