package dev.linkcentral.presentation.response.member;

import dev.linkcentral.service.dto.member.MemberInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoResponse {

    private Long userId;
    private String name;
    private String email;
    private String nickname;

    public static MemberInfoResponse toMemberInfoResponse(MemberInfoDTO memberInfoDTO) {
        return MemberInfoResponse.builder()
                .userId(memberInfoDTO.getUserId())
                .name(memberInfoDTO.getName())
                .email(memberInfoDTO.getEmail())
                .nickname(memberInfoDTO.getNickname())
                .build();
    }
}
