package dev.linkcentral.presentation.response.member;

import dev.linkcentral.infrastructure.jwt.TokenDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LoginSuccessResponse {

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String redirectUrl;

    public static LoginSuccessResponse toLoginSuccessResponse(TokenDTO jwtTokenDTO, String redirectUrl) {
        return LoginSuccessResponse.builder()
                .grantType(jwtTokenDTO.getGrantType())
                .accessToken(jwtTokenDTO.getAccessToken())
                .refreshToken(jwtTokenDTO.getRefreshToken())
                .redirectUrl(redirectUrl)
                .build();
    }
}