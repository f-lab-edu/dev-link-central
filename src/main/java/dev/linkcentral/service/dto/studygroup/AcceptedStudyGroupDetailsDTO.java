package dev.linkcentral.service.dto.studygroup;

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
