package dev.linkcentral.infrastructure.jwt;

import org.springframework.security.core.Authentication;

public interface TokenProvider {

    JwtTokenDTO generateToken(Authentication authentication);

    Authentication getAuthentication(String accessToken);

    boolean validateToken(String JwtToken);
}
