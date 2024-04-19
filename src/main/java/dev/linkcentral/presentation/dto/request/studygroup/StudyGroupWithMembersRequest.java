package dev.linkcentral.presentation.dto.request.studygroup;

import dev.linkcentral.presentation.dto.request.member.MemberRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyGroupWithMembersRequest {

    private Long id;
    private Long leaderId;
    private String groupName;
    private List<MemberRequest> members;

}
