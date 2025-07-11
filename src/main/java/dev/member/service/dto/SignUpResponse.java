package dev.member.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpResponse {

    private Long memberId;

    public static SignUpResponse from(Long memberId) {
        return new SignUpResponse(memberId);
    }
}
