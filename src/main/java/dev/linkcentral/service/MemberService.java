package dev.linkcentral.service;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.repository.MemberRepository;
import dev.linkcentral.service.dto.request.MemberMailRequestDTO;
import dev.linkcentral.service.dto.request.MemberSaveRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private static final String FROM_ADDRESS = "alstjr706@gmail.com";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public Long joinMember(MemberSaveRequestDTO member) {
        member.updateRole("USER");
        Member memberEntity = Member.builder()
                .name(member.getName())
                .password(member.getPassword())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole())
                .build();

        return memberRepository.save(memberEntity).getId();
    }

    public Optional<Member> loginMember(String name, String password) {
        List<Member> memberList = memberRepository.findByName(name);

        for (Member member : memberList) {
            if (passwordEncoder.matches(password, member.getPassword())) {
                return Optional.of(member);
            }
        }
        return Optional.empty();
    }

    public boolean isNicknameDuplicated(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    public boolean userEmailCheck(String userEmail, String userName) {
        Optional<Member> member = memberRepository.findByEmail(userEmail);

        if (member.isPresent() && member.get().getName().equals(userName)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * DTO에 사용자가 원하는 내용과 제목을 저장
     */
    @Transactional
    public MemberMailRequestDTO createMailAndChangePassword(String userEmail, String userName) {
        MemberMailRequestDTO dto = new MemberMailRequestDTO();
        String str = generateTempPassword();

        dto.setAddress(userEmail);
        dto.setTitle(userName + "님의 HOTTHINK 임시비밀번호 안내 이메일 입니다.");
        dto.setMessage("안녕하세요. HOTTHINK 임시비밀번호 안내 관련 이메일 입니다."
                + "[" + userName + "]" + "님의 임시 비밀번호는 " + str + " 입니다.");

        updatePassword(str, userEmail);
        return dto;
    }

    // 이메일로 발송된 임시비밀번호로 해당 유저의 패스워드 변경
    @Transactional
    public void updatePassword(String str, String userEmail) {

        String pw = passwordEncoder.encode(str);
        Optional<Member> member = memberRepository.findByEmail(userEmail);

        if (member.isPresent()) {
            Long id = member.get().getId();
            memberRepository.updatePasswordById(id, pw);
        }
    }

    // 10자리의 랜덤난수를 생성하는 메소드
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

    public void mailSend(MemberMailRequestDTO mailDto) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(mailDto.getAddress());    // 받는사람 주소
        message.setFrom(FROM_ADDRESS);          // 보내는 사람 주소
        message.setSubject(mailDto.getTitle()); // 메일 제속
        message.setText(mailDto.getMessage());  // 메일 내용
        mailSender.send(message);
    }

    public void updateMember(Member member) {
        Member foundMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> {
                    return new IllegalArgumentException("회원 찾기 실패");
                });

        String password = member.getPassword();
        String encodePassword = passwordEncoder.encode(password); //시큐리티

        foundMember.updatePassword(encodePassword);
        foundMember.updateEmail(member.getEmail());
        foundMember.updateNickname(member.getNickname());
    }

    public boolean checkPassword(String nickname, String password) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        if (passwordEncoder.matches(password, member.getPassword())) {
            return true;
        }
        return false;
    }

}