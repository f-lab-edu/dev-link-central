package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.dto.member.*;
import dev.linkcentral.service.dto.token.MemberDetailsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberMapper {

    private final PasswordEncoder passwordEncoder;

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

    public Member createMemberFromDTO(MemberRegistrationDTO memberDTO, List<String> roles) {
        return Member.builder()
                .name(memberDTO.getName())
                .passwordHash(passwordEncoder.encode(memberDTO.getPassword()))
                .email(memberDTO.getEmail())
                .nickname(memberDTO.getNickname())
                .roles(roles)
                .build();
    }

    public MemberRegistrationResultDTO toMemberRegistrationResultDTO(Long memberId) {
        return new MemberRegistrationResultDTO(memberId);
    }

    public MemberEditFormDTO toCurrentMember(Member currentMember) {
        return new MemberEditFormDTO(currentMember);
    }

    public MemberDetailsDTO toMemberDetailsDTO(Member member) {
        if (member == null) {
            return null;
        }

        return new MemberDetailsDTO(
                member.getId(),
                member.getPassword(),
                member.getName(),
                member.getEmail(),
                member.getRoles(),
                member.isDeleted()
        );
    }

    public UserDetails createUserDetails(MemberDetailsDTO userDTO) {
        return User.builder()
                .username(userDTO.getName())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .roles(userDTO.getRoles().toArray(new String[0]))
                .build();
    }
}
