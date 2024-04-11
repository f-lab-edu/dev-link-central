package dev.linkcentral.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupCreateResponse {

    private Long id;
    private String groupName;
    private String studyTopic;

}
