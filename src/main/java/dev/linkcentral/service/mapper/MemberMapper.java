package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.entity.MemberStatus;
import dev.linkcentral.infrastructure.jwt.JwtTokenDTO;
import dev.linkcentral.presentation.request.member.MemberDeleteRequest;
import dev.linkcentral.presentation.request.member.MemberLoginRequest;
import dev.linkcentral.presentation.response.member.*;
import dev.linkcentral.service.dto.member.*;
import dev.linkcentral.presentation.request.member.MemberEditRequest;
import dev.linkcentral.presentation.request.member.MemberSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberMapper {

    private final PasswordEncoder passwordEncoder;

    public MemberEditDTO toMemberEditCommand(MemberEditRequest request) {
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

    public MemberRegistrationDTO toMemberRegistrationCommand(MemberSaveRequest request) {
        return MemberRegistrationDTO.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .roles(Collections.singletonList(String.valueOf(MemberStatus.USER)))
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

    public LoginSuccessResponse toLoginSuccessResponse(JwtTokenDTO jwtTokenDTO, String redirectUrl) {
        return LoginSuccessResponse.builder()
                .grantType(jwtTokenDTO.getGrantType())
                .accessToken(jwtTokenDTO.getAccessToken())
                .refreshToken(jwtTokenDTO.getRefreshToken())
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

    public MemberDeleteRequestDTO toMemberDeleteRequestCommand(MemberDeleteRequest request) {
        return new MemberDeleteRequestDTO(request.getPassword());
    }

    public MemberRegistrationResultDTO toMemberRegistrationResultDTO(Long memberId) {
        return new MemberRegistrationResultDTO(memberId);
    }

    public MemberLoginRequestDTO toMemberLoginRequestCommand(MemberLoginRequest memberLoginRequest) {
        return new MemberLoginRequestDTO(
                memberLoginRequest.getEmail(),
                memberLoginRequest.getPassword());
    }

    public MemberPasswordResponse toMemberPasswordResponse(boolean pwFindCheck) {
        return new MemberPasswordResponse(pwFindCheck);
    }

    public MemberSaveResponse toMemberSaveResponse(MemberRegistrationResultDTO registrationResultDTO) {
        return new MemberSaveResponse(registrationResultDTO.getMemberId(), "회원 등록 성공");
    }

    public MailPasswordResetResponse toMailPasswordResetResponse() {
        return new MailPasswordResetResponse(true, "임시 비밀번호가 이메일로 발송되었습니다.");
    }

    public MemberEditFormDTO toCurrentMember(Member currentMember) {
        return new MemberEditFormDTO(currentMember);
    }

    public MemberEditResponse toupdateMemberResponse() {
        return new MemberEditResponse(200, "업데이트 성공되었습니다.");
    }

    public MemberDeleteResponse toSoftDeleteMemberResponse(boolean softDeleteMember) {
        if (softDeleteMember) {
            return new MemberDeleteResponse(true, "회원 탈퇴가 완료되었습니다.");
        }
        return new MemberDeleteResponse(false, "회원 탈퇴 처리 중 오류가 발생했습니다.");
    }
}
