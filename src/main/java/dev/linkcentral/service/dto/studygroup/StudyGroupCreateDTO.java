package dev.linkcentral.service.dto.studygroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupCreateDTO {

    private String groupName;
    private String studyTopic;

}
