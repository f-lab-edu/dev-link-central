package dev.linkcentral.infrastructure.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class JwtTokenDTO {

    private String grantType;
    private String accessToken;
    private String refreshToken;
}