package dev.linkcentral.infrastructure.jwt;

import dev.linkcentral.infrastructure.jwt.TokenDTO;
import org.springframework.security.core.Authentication;

public interface TokenProvider {

    TokenDTO generateToken(Authentication authentication);

    Authentication getAuthentication(String accessToken);

    boolean validateToken(String token);
}
