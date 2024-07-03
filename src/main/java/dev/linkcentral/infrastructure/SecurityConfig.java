package dev.linkcentral.infrastructure;

import dev.linkcentral.infrastructure.jwt.JwtAuthenticationFilter;
import dev.linkcentral.infrastructure.jwt.TokenProvider;
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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
                        "/api/v1/member/**",
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
