package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.infrastructure.jwt.JwtTokenDTO;
import dev.linkcentral.presentation.dto.MemberEditDTO;
import dev.linkcentral.presentation.dto.MemberInfoDTO;
import dev.linkcentral.presentation.dto.MemberRegistrationDTO;
import dev.linkcentral.presentation.dto.request.MemberEditRequest;
import dev.linkcentral.presentation.dto.request.MemberSaveRequest;
import dev.linkcentral.presentation.dto.response.MemberInfoResponse;
import dev.linkcentral.presentation.response.LoginSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberMapper {

    private final PasswordEncoder passwordEncoder;

    public MemberEditDTO toMemberEditDTO(MemberEditRequest request) {
        return MemberEditDTO.builder()
                .id(request.getId())
                .name(request.getName())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .build();
    }

    public void updateMemberFromEditDTO(Member memberEntity, MemberEditDTO memberEditDTO) {
        if (memberEditDTO.getPassword() != null && !memberEditDTO.getPassword().isEmpty()) {
            memberEntity.updatePasswordHash(passwordEncoder.encode(memberEditDTO.getPassword()));
        }
        memberEntity.updateName(memberEditDTO.getName());
        memberEntity.updateNickname(memberEditDTO.getNickname());
    }

    public MemberInfoDTO toMemberInfoDTO(Member member) {
        return MemberInfoDTO.builder()
                .userId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }

    public MemberRegistrationDTO toMemberRegistrationDTO(MemberSaveRequest request) {
        return MemberRegistrationDTO.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .roles(Collections.singletonList("USER"))
                .build();
    }

    public Member createMemberFromDTO(MemberRegistrationDTO memberDTO, List<String> roles) {
        return Member.builder()
                .name(memberDTO.getName())
                .passwordHash(passwordEncoder.encode(memberDTO.getPassword()))
                .email(memberDTO.getEmail())
                .nickname(memberDTO.getNickname())
                .roles(roles)
                .build();
    }

    public LoginSuccessResponse toLoginSuccessResponse(JwtTokenDTO jwtToken, String redirectUrl) {
        return LoginSuccessResponse.builder()
                .grantType(jwtToken.getGrantType())
                .accessToken(jwtToken.getAccessToken())
                .refreshToken(jwtToken.getRefreshToken())
                .redirectUrl(redirectUrl)
                .build();
    }

    public MemberInfoResponse toMemberInfoResponse(MemberInfoDTO dto) {
        return MemberInfoResponse.builder()
                .userId(dto.getUserId())
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .build();
    }

}
