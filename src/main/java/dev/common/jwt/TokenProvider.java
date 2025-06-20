package dev.common.jwt;

import org.springframework.security.core.Authentication;

/**
 * 토큰 제공자 인터페이스
 * JWT 토큰의 생성, 인증 객체 추출, 토큰 유효성 검사를 위한 메서드를 정의합니다.
 */
public interface TokenProvider {

    /**
     * 인증 객체로부터 JWT 토큰을 생성합니다.
     *
     * @param authentication 인증 객체
     * @return 생성된 JWT 토큰 DTO
     */
    JwtToken generateToken(Authentication authentication);

    /**
     * JWT 토큰에서 인증 객체를 추출합니다.
     *
     * @param accessToken JWT 액세스 토큰
     * @return 추출된 인증 객체
     */
    Authentication getAuthentication(String accessToken);

    /**
     * JWT 토큰의 유효성을 검사합니다.
     *
     * @param jwtToken JWT 토큰
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    boolean validateToken(String JwtToken);
}
