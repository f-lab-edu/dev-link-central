package dev.common;

import dev.common.jwt.JwtAuthenticationFilter;
import dev.common.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스
 * JWT 인증 필터와 패스워드 인코더 설정을 포함합니다.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;


    /**
     * 비밀번호 인코더를 Bean으로 등록합니다.
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * SecurityFilterChain을 설정합니다.
     * @param httpSecurity HttpSecurity 객체
     * @return SecurityFilterChain 인스턴스
     * @throws Exception 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .antMatchers(
                        "/",
                        "/favicon.ico",
                        "/api/v1/public/**",
                        "/api/v1/view/**",
                        "/static/**",
                        "/css/**",
                        "/images/**"
                )
                .permitAll()
                .antMatchers(
                        "/api/v1/members/**",
                        "/api/v1/article/**",
                        "/api/v1/profile/**",
                        "/api/v1/friends/**",
                        "/api/v1/study-group/**",
                        "/api/v1/group-feed/**"
                ).hasAuthority("USER") // `USER` 권한을 가진 사용자만 접근 가능하도록 설정
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        httpSecurity.cors();
        return httpSecurity.build();
    }
}
