package dev.linkcentral.service.dto.studygroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyGroupMembersDetailDTO {

    private Long id;
    private Long leaderId;
    private String groupName;
    private List<StudyGroupMemberBasicInfoDTO> members;

}