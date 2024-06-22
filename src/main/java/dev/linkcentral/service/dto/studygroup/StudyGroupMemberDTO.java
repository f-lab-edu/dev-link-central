package dev.linkcentral.service.dto.studygroup;

import dev.linkcentral.database.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupMemberDTO {

    private Member member;
}
