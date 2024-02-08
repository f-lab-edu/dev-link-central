package dev.linkcentral.infrastructure;

import dev.linkcentral.infrastructure.jwt.JwtAuthenticationFilter;
import dev.linkcentral.infrastructure.jwt.JwtTokenProvider;
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

    private final JwtTokenProvider jwtTokenProvider;

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
                        "/members/login",
                        "/api/v1/member/join-form",
                        "/api/v1/member/register",
                        "/api/v1/member/login",
                        "/api/v1/member/{nickname}/exists",
                        "/api/v1/member/reset-password",
                        "/api/v1/member/forgot-password",
                        "/api/v1/member/send-email/update-password",
                        "/api/v1/member/check-current-password",
                        "/api/v1/member/login-success"
                ).permitAll()
                .antMatchers(
                        "/api/v1/member/edit-form",
                        "/api/v1/member/edit",
                        "/api/v1/member/delete-page",
                        "/api/v1/member/delete",
                        "/api/v1/article/save"
                ).hasAuthority("USER")
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        httpSecurity.cors();
        return httpSecurity.build();
    }
}