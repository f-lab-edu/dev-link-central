package dev.linkcentral.service;

import dev.linkcentral.common.exception.DuplicateEmailException;
import dev.linkcentral.common.exception.DuplicateNicknameException;
import dev.linkcentral.common.exception.MemberRegistrationException;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.repository.MemberRepository;
import dev.linkcentral.service.dto.request.MemberEditRequestDTO;
import dev.linkcentral.service.dto.request.MemberMailRequestDTO;
import dev.linkcentral.service.dto.request.MemberSaveRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private static final String FROM_ADDRESS = "alstjr706@gmail.com";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public Member joinMember(MemberSaveRequestDTO memberDTO) {
        String nickname = memberDTO.getNickname();
        checkForDuplicate(memberDTO, nickname);

        try {
            memberDTO.updateRole("USER");
            Member memberEntity = getMemberEntity(memberDTO);
            return memberRepository.save(memberEntity);
        } catch (Exception e) {
            throw new MemberRegistrationException("회원 등록 중 오류가 발생했습니다.", e);
        }
    }

    private Member getMemberEntity(MemberSaveRequestDTO memberDTO) {
        return Member.builder()
                .name(memberDTO.getName())
                .passwordHash(passwordEncoder.encode(memberDTO.getPassword()))
                .email(memberDTO.getEmail())
                .nickname(memberDTO.getNickname())
                .role(memberDTO.getRole())
                .build();
    }

    private void checkForDuplicate(MemberSaveRequestDTO memberDTO, String nickname) {
        if (memberRepository.existsByNicknameAndDeletedFalse(nickname)) {
            throw new DuplicateNicknameException("닉네임이 이미 사용 중입니다.");
        }

        if (memberRepository.countByEmailIgnoringDeleted(memberDTO.getEmail()) > 0) {
            throw new DuplicateEmailException("중복된 이메일 주소입니다.");
        }
    }

    public Optional<Member> loginMember(String email, String password) {
        Optional<Member> member = memberRepository.findByEmailAndDeletedFalse(email);
        if (member.isPresent()) {
            String passwordHash = member.get().getPasswordHash();

            if (passwordEncoder.matches(password, passwordHash)) {
                return member;
            }
        }
        return Optional.empty();
    }

    public boolean isNicknameDuplicated(String nickname) {
        return memberRepository.existsByNicknameAndDeletedFalse(nickname);
    }

    public boolean userEmailCheck(String userEmail, String userName) {
        Optional<Member> member = memberRepository.findByEmailAndDeletedFalse(userEmail);
        String memberName = member.get().getName();

        return memberName.equals(userName);
    }

    /**
     * DTO에 사용자가 원하는 내용과 제목을 저장
     */
    @Transactional
    public MemberMailRequestDTO createMailAndChangePassword(String userEmail, String userName) {
        MemberMailRequestDTO dto = new MemberMailRequestDTO();
        String generatedPassword = generateTempPassword();

        dto.setAddress(userEmail);
        dto.setTitle(userName + "님의 HOTTHINK 임시비밀번호 안내 이메일 입니다.");
        dto.setMessage("안녕하세요. HOTTHINK 임시비밀번호 안내 관련 이메일 입니다."
                + "[" + userName + "]" + "님의 임시 비밀번호는 " + generatedPassword + " 입니다.");

        updatePassword(generatedPassword, userEmail);
        return dto;
    }

    /**
     * 이메일로 발송된 임시비밀번호로 해당 유저의 패스워드 변경
     */
    @Transactional
    public void updatePassword(String generatedPassword, String userEmail) {
        String passwordHash = passwordEncoder.encode(generatedPassword);
        Optional<Member> member = memberRepository.findByEmailAndDeletedFalse(userEmail);

        if (member.isPresent()) {
            Long id = member.get().getId();
            memberRepository.updatePasswordById(id, passwordHash);
        }
    }

    /**
     * 10자리의 랜덤난수를 생성하는 메소드
     */
    public String generateTempPassword() {
        char[] charSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        StringBuilder str = new StringBuilder();

        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str.append(charSet[idx]);
        }
        return str.toString();
    }

    /**
     * 메일 보내기
     */
    public void mailSend(MemberMailRequestDTO mailDto) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(mailDto.getAddress());    // 받는사람 주소
        message.setFrom(FROM_ADDRESS);          // 보내는 사람 주소
        message.setSubject(mailDto.getTitle()); // 메일 제속
        message.setText(mailDto.getMessage());  // 메일 내용
        mailSender.send(message);
    }

    public void updateMember(MemberEditRequestDTO memberEditDTO) {
        Member memberEntity = memberRepository.findById(memberEditDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원 찾기 실패"));

        String password = memberEditDTO.getPassword();
        memberEntity.updatePasswordHash(passwordEncoder.encode(password));
        memberEntity.updateName(memberEditDTO.getName());
        memberEntity.updateNickname(memberEditDTO.getNickname());
    }

    public boolean checkPassword(String nickname, String password) {
        Optional<Member> member = memberRepository.findByNicknameAndDeletedFalse(nickname);

        if (member.isPresent()) {
            String passwordHash = member.get().getPasswordHash();
            return passwordEncoder.matches(password, passwordHash);
        }
        return false;
    }

    @Transactional
    public boolean deleteMember(String nickname, String password) {
        Member member = memberRepository.findByNicknameAndDeletedFalse(nickname)
                .orElseThrow(() -> new IllegalArgumentException("닉네임이 존재하지 않습니다."));

        if (member != null) {
            String passwordHash = member.getPasswordHash();

            if (passwordEncoder.matches(password, passwordHash)) {
                memberRepository.softDeleteById(member.getId());
                return true;
            }
        }
        return false;
    }

}