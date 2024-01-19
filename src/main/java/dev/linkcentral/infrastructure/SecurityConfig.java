package dev.linkcentral.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()                 // CSRF 방지
                .formLogin().disable()            // 기본 로그인 페이지 없애기
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .antMatchers("/articles/save").authenticated() // 게시글 작성 경로에 대한 접근은 인증된 사용자에게만 허용
                .and()
                .logout()                         // 로그아웃 설정 추가
                    .logoutSuccessUrl("/")        // 로그아웃 성공 시 리다이렉트할 URL
                    .invalidateHttpSession(true)  // 세션 무효화
                    .deleteCookies("JSESSIONID")  // JSESSIONID 쿠키 삭제
                    .permitAll();
        return http.build();
    }
}