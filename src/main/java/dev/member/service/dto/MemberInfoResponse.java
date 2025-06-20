package dev.member.service.dto;

import dev.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoResponse {

    private Long userId;
    private String name;
    private String email;
    private String nickname;

    public static MemberInfoResponse from(Member member) {
        return MemberInfoResponse.builder()
                .userId(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }
}
