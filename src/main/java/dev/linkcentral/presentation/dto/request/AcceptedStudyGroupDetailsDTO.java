package dev.linkcentral.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcceptedStudyGroupDetailsDTO {

    private Long id;
    private String groupName;
    private String studyTopic;

}
