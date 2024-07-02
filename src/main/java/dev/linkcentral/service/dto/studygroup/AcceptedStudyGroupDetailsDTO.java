package dev.linkcentral.service.dto.studygroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcceptedStudyGroupDetailsDTO {

    private Long id;
    private String groupName;
    private String studyTopic;
    private int memberCount;
    private Long leaderId;
    private List<StudyGroupUserDTO> members;
}
