package dev.member.service;

import dev.common.jwt.JwtToken;
import dev.common.jwt.TokenProvider;
import dev.member.controller.request.SignInRequest;
import dev.member.entity.Member;
import dev.member.exception.MemberError;
import dev.member.exception.MemberException;
import dev.member.service.dto.MemberInfoResponse;
import dev.member.service.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberAuthService {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public MemberInfoResponse info() {
        Member member = authenticated();
        return MemberInfoResponse.from(member);
    }

    public Member authenticated() {
        String email = SecurityUtils.getCurrentUserUsername();
        return memberService.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean checkPassword(String password) {
        Member member = authenticated();
        return passwordEncoder.matches(password, member.getPasswordHash());
    }

    @Transactional
    public JwtToken signIn(SignInRequest signInRequest) {
        Member member = memberService.findByEmail(signInRequest.getEmail());
        validatePassword(signInRequest.getPassword(), member.getPasswordHash());
        Authentication authentication = createAuthentication(member, signInRequest.getPassword());
        return tokenProvider.generateToken(authentication);
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new MemberException(MemberError.PASSWORD_NOT_MATCHED);
        }
    }

    private Authentication createAuthentication(Member member, String password) {
        List<SimpleGrantedAuthority> authorities = member.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(member.getEmail(), password, authorities);
    }

    @Transactional(readOnly = true)
    public Member current() {
        String email = currentEmail();
        return memberService.findByEmail(email);
    }

    private String currentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new MemberException(MemberError.MEMBER_NOT_FOUND);
        }
        return ((UserDetails) authentication.getPrincipal()).getUsername();
    }
}
