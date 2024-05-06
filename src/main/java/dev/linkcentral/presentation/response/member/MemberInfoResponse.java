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
    private String email;
    private String nickname;

    public static MemberInfoResponse toMemberInfoResponse(MemberInfoDTO dto) {
        return MemberInfoResponse.builder()
                .userId(dto.getUserId())
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .build();
    }
}
