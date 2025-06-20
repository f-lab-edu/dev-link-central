package dev.member.service;

import dev.member.controller.request.SignUpRequest;
import dev.member.controller.request.UpdateMemberRequest;
import dev.member.entity.Member;
import dev.member.exception.MemberError;
import dev.member.exception.MemberException;
import dev.member.repository.MemberRepository;
import dev.member.service.dto.SignUpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MemberAuthService memberAuthService;

    @Transactional(readOnly = true)
    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberError.MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new MemberException(MemberError.EMAIL_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return memberRepository.findByEmailAndDeletedFalse(email).isPresent();
    }

    @Transactional
    public SignUpResponse signup(SignUpRequest signUpRequest) {
        validateDuplicate(signUpRequest.getEmail(), signUpRequest.getNickname());
        Member memberEntity = signUpRequest.toEntity(passwordEncoder);
        Member savedMember = memberRepository.save(memberEntity);
        return SignUpResponse.from(savedMember.getId());
    }

    @Transactional
    public void update(UpdateMemberRequest updateRequest) {
        Member member = findById(updateRequest.getId());
        if (!passwordEncoder.matches(updateRequest.getCurrentPassword(), member.getPasswordHash())) {
            throw new MemberException(MemberError.PASSWORD_NOT_MATCHED);
        }
        member.updateName(updateRequest.getName());
        member.updateNickname(updateRequest.getNickname());
        if (updateRequest.getNewPassword() != null && !updateRequest.getNewPassword().isBlank()) {
            member.changePassword(updateRequest.getNewPassword(), passwordEncoder);
        }
    }

    @Transactional
    public void softDelete(String password) {
        Member member = memberAuthService.current();
        if (!passwordEncoder.matches(password, member.getPasswordHash())) {
            throw new MemberException(MemberError.PASSWORD_NOT_MATCHED);
        }
        memberRepository.softDeleteById(member.getId());
    }

    @Transactional
    public void sendPasswordResetMail(String email) {
        mailService.sendPasswordResetMail(email);
    }

    private void validateDuplicate(String email, String nickname) {
        if (memberRepository.existsByNicknameAndDeletedFalse(nickname)) {
            throw new MemberException(MemberError.NICKNAME_DUPLICATED);
        }
        if (memberRepository.countByEmailIgnoringDeleted(email) > 0) {
            throw new MemberException(MemberError.EMAIL_DUPLICATED);
        }
    }
}
