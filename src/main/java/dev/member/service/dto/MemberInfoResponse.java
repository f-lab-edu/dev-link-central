package dev.member.service.dto;

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
}
