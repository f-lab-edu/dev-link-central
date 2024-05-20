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
public class GroupFeedInfoResponse {

    private Long memberId;
    private String name;
    private String nickname;

    public static GroupFeedInfoResponse toGroupFeedInfoResponse(MemberCurrentDTO memberCurrentDTO) {
        return GroupFeedInfoResponse.builder()
                .memberId(memberCurrentDTO.getMemberId())
                .name(memberCurrentDTO.getName())
                .nickname(memberCurrentDTO.getNickname())
                .build();
    }
}
