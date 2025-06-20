package dev.member.mapper;

import dev.linkcentral.service.dto.token.MemberDetailsDTO;
import dev.member.constant.MemberRole;
import dev.member.controller.request.SignUpRequest;
import dev.member.entity.Member;
import dev.member.service.dto.MemberInfoResponse;
import dev.member.service.dto.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MemberMapper {

    private final PasswordEncoder passwordEncoder;

    public MemberInfoResponse toMemberInfoDto(Member member) {
        return MemberInfoResponse.builder()
                .userId(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }

    public Member mapSignUpDtoToMember(SignUpRequest memberDTO) {
        Set<MemberRole> memberRoles = convertToMemberRoles(memberDTO.getRoles());
        return Member.builder()
                .name(memberDTO.getName())
                .passwordHash(passwordEncoder.encode(memberDTO.getPassword()))
                .email(memberDTO.getEmail())
                .nickname(memberDTO.getNickname())
                .roles(memberRoles)
                .build();
    }

    private Set<MemberRole> convertToMemberRoles(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of(MemberRole.USER);
        }
        return roles.stream()
                .map(MemberRole::valueOf)
                .collect(Collectors.toSet());
    }

    public SignUpResponse toMemberSignUpResultDto(Long memberId) {
        return new SignUpResponse(memberId);
    }

    public MemberDetailsDTO toMemberDetailsDto(Member member) {
        List<String> roleNames = member.getRoles().stream()
                .map(MemberRole::name)
                .toList();

        return MemberDetailsDTO.builder()
                .id(member.getId())
                .password(member.getPasswordHash())
                .name(member.getName())
                .email(member.getEmail())
                .roles(roleNames)
                .isDeleted(member.isDeleted())
                .build();
    }

    public UserDetails toUserDetails(MemberDetailsDTO userDTO) {
        return User.builder()
                .username(userDTO.getName())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .roles(userDTO.getRoles().toArray(new String[0]))
                .build();
    }
}
