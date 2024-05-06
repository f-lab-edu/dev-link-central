package dev.linkcentral.service.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Authentication 객체가 존재하는지 확인
        boolean isAuthenticationNotNull = authentication != null;

        // 현재 사용자가 익명 사용자인지 확인
        boolean isNotAnonymousUser = !(authentication instanceof AnonymousAuthenticationToken);

        // 현재 사용자가 인증되었는지 확인
        boolean isAuthenticated = authentication.isAuthenticated();

        // 모든 조건이 참이어야 사용자가 인증된 것으로 간주
        return isAuthenticationNotNull && isNotAnonymousUser && isAuthenticated;
    }

    public static String getCurrentUserUsername() {
        if (isAuthenticated()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                return userDetails.getUsername(); // 여기서는 사용자의 이메일
            }
        }
        return null; // 또는 적절한 예외 처리
    }
}
