package dev.linkcentral.infrastructure.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * JWT 토큰의 유형, 액세스 토큰, 리프레시 토큰을 저장합니다.
 */
@Data
@Builder
@AllArgsConstructor
public class JwtTokenDTO {

    private String grantType;
    private String accessToken;
    private String refreshToken;
}
