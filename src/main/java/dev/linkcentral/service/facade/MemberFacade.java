package dev.linkcentral.service.facade;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.infrastructure.jwt.JwtTokenDTO;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.dto.member.*;
import dev.linkcentral.service.dto.token.MemberDetailsDTO;
import dev.linkcentral.service.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberFacade {

    private final MemberService memberService;
    private final MemberMapper memberMapper;

    public MemberEditFormDTO memberEditForm() {
        Member currentMember = memberService.getCurrentMember();
        return memberMapper.toCurrentMember(currentMember);
    }

    public MemberInfoDTO getMemberInfo() {
        return memberService.getCurrentUserInfo();
    }

    public void updateMember(MemberEditDTO memberEditDTO) {
        memberService.editMember(memberEditDTO);
    }

    public boolean softDeleteMember(MemberDeleteRequestDTO memberDeleteRequestDTO) {
        Member member = memberService.getCurrentMember();
        return memberService.removeMember(member.getNickname(), memberDeleteRequestDTO.getPassword());
    }

    public MemberRegistrationResultDTO registerNewMember(MemberRegistrationDTO memberDTO) {
        Member member = memberService.registerMember(memberDTO);
        return memberMapper.toMemberRegistrationResultDTO(member.getId());
    }

    public JwtTokenDTO loginMember(MemberLoginRequestDTO memberLoginRequestDTO) {
        return memberService.authenticateAndGenerateJwtToken(
                memberLoginRequestDTO.getEmail(),
                memberLoginRequestDTO.getPassword());
    }

    public boolean isPasswordValid(String userEmail) {
        return memberService.validateUserEmail(userEmail);
    }

    public void sendPasswordResetEmail(String userEmail) {
        MemberMailDTO memberMailDTO = memberService.createMailForPasswordReset(userEmail);
        memberService.sendMail(memberMailDTO);
    }

    public boolean checkPassword(String password) {
        Member member = memberService.getCurrentMember();
        return memberService.validatePassword(member.getNickname(),password);
    }

    public MemberDetailsDTO findByEmailAndDeletedFalse(String email) {
        Member member = memberService.findByEmailAndDeletedFalse(email);
        return memberMapper.toMemberDetailsDTO(member);
    }

}
