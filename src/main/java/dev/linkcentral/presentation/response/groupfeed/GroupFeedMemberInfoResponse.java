package dev.linkcentral.presentation.response.groupfeed;

import dev.linkcentral.service.dto.member.MemberCurrentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupFeedMemberInfoResponse {

    private Long memberId;
    private String name;
    private String nickname;

    public static GroupFeedMemberInfoResponse toGroupFeedInfoResponse(MemberCurrentDTO memberCurrentDTO) {
        return GroupFeedMemberInfoResponse.builder()
                .memberId(memberCurrentDTO.getMemberId())
                .name(memberCurrentDTO.getName())
                .nickname(memberCurrentDTO.getNickname())
                .build();
    }
}
