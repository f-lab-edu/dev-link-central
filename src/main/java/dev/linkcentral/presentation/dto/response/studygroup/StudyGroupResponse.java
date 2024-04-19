package dev.linkcentral.presentation.dto.response.studygroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupResponse {

    private Long id;
    private String groupName;
    private String studyTopic;

}
