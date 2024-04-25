package dev.linkcentral.service;

import dev.linkcentral.common.exception.DuplicateEmailException;
import dev.linkcentral.common.exception.DuplicateNicknameException;
import dev.linkcentral.common.exception.MemberEmailNotFoundException;
import dev.linkcentral.common.exception.MemberRegistrationException;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.entity.MemberStatus;
import dev.linkcentral.database.repository.MemberRepository;
import dev.linkcentral.infrastructure.SecurityUtils;
import dev.linkcentral.infrastructure.jwt.JwtTokenDTO;
import dev.linkcentral.infrastructure.jwt.JwtTokenProvider;
import dev.linkcentral.presentation.dto.MemberEditDTO;
import dev.linkcentral.presentation.dto.MemberInfoDTO;
import dev.linkcentral.presentation.dto.MemberMailDTO;
import dev.linkcentral.presentation.dto.MemberRegistrationDTO;
import dev.linkcentral.service.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private static final String FROM_ADDRESS = "alstjr706@gmail.com";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberMapper memberMapper;

    public MemberInfoDTO getCurrentUserInfo() {
        Member member = getCurrentMember();
        return memberMapper.toMemberInfoDTO(member);
    }

    @Transactional(readOnly = true)
    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("ID가 있는 회원을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public Member getCurrentMember() {
        String email = SecurityUtils.getCurrentUserUsername();
        return memberRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public Member getAuthenticatedMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        return memberRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
    }

    @Transactional
    public JwtTokenDTO authenticateAndGenerateJwtToken(String username, String password) {
        Member member = memberRepository.findByEmailAndDeletedFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, member.getPasswordHash())) {
            throw new IllegalArgumentException("아이디 혹은 비밀번호가 일치하지 않습니다.");
        }

        Collection<? extends GrantedAuthority> authorities = member.getAuthorities();
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password, authorities);
        return jwtTokenProvider.generateToken(authentication);
    }

    @Transactional
    public Member registerMember(MemberRegistrationDTO memberDTO) {
        String nickname = memberDTO.getNickname();
        validateForDuplicates(memberDTO, nickname);

        try {
            List<String> roles = new ArrayList<>();
            roles.add(String.valueOf(MemberStatus.USER));
            Member memberEntity = memberMapper.createMemberFromDTO(memberDTO, roles);
            return memberRepository.save(memberEntity);
        } catch (Exception e) {
            throw new MemberRegistrationException("회원 등록 중 오류가 발생했습니다.", e);
        }
    }

    private void validateForDuplicates(MemberRegistrationDTO memberDTO, String nickname) {
        if (memberRepository.existsByNicknameAndDeletedFalse(nickname)) {
            throw new DuplicateNicknameException("닉네임이 이미 사용 중입니다.");
        }

        if (memberRepository.countByEmailIgnoringDeleted(memberDTO.getEmail()) > 0) {
            throw new DuplicateEmailException("중복된 이메일 주소입니다.");
        }
    }

    @Transactional(readOnly = true)
    public boolean validateNicknameDuplication(String nickname) {
        boolean isDuplicated = memberRepository.existsByNicknameAndDeletedFalse(nickname);

        if (isDuplicated) {
            throw new DuplicateNicknameException("닉네임이 중복되었습니다.: " + nickname);
        }
        return isDuplicated;
    }

    @Transactional(readOnly = true)
    public boolean validateUserEmail(String userEmail, String userName) {
        Optional<Member> member = memberRepository.findByEmailAndNameAndDeletedFalse(userEmail, userName);

        if (member.isEmpty()) {
            throw new MemberEmailNotFoundException("유저의 이메일을 찾을 수 없습니다.");
        }
        return true;
    }

    @Transactional
    public MemberMailDTO createMailForPasswordReset(String userEmail, String userName) {
        String generatedPassword = createTemporaryPassword();

        MemberMailDTO memberMailDTO = MemberMailDTO.builder()
                .address(userEmail)
                .title(userName + "님의 HOTTHINK 임시비밀번호 안내 이메일 입니다.")
                .message("안녕하세요. HOTTHINK 임시비밀번호 안내 관련 이메일 입니다." +
                        "[" + userName + "]" + "님의 임시 비밀번호는 " + generatedPassword + " 입니다.")
                .build();

        resetPassword(generatedPassword, userEmail);
        return memberMailDTO;
    }

    @Transactional
    public void resetPassword(String generatedPassword, String userEmail) {
        String passwordHash = passwordEncoder.encode(generatedPassword);
        Optional<Member> member = memberRepository.findByEmailAndDeletedFalse(userEmail);

        if (member.isPresent()) {
            Long id = member.get().getId();
            memberRepository.updatePasswordById(id, passwordHash);
        }
    }

    public String createTemporaryPassword() {
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

    public void sendMail(MemberMailDTO memberMailDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        try {
            message.setTo(memberMailDTO.getAddress());    // 받는사람 주소
            message.setFrom(FROM_ADDRESS);                // 보내는 사람 주소
            message.setSubject(memberMailDTO.getTitle()); // 메일 제목
            message.setText(memberMailDTO.getMessage());  // 메일 내용

            mailSender.send(message);
            log.info("이메일이 성공적으로 전송되었습니다: {}", memberMailDTO.getAddress());
        } catch (MailException ex) {
            log.error("이메일을 보내지 못했습니다: {}: {}", memberMailDTO.getAddress(), ex.getMessage());
            throw new RuntimeException("이메일 전송 실패", ex);
        }
    }

    @Transactional
    public void editMember(MemberEditDTO memberEditDTO) {
        validateMemberEditDTO(memberEditDTO);

        Member memberEntity = memberRepository.findById(memberEditDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원 찾기 실패"));

        memberMapper.updateMemberFromEditDTO(memberEntity, memberEditDTO);
    }

    private void validateMemberEditDTO(MemberEditDTO memberEditDTO) {
        if (memberEditDTO.getId() == null) {
            throw new IllegalArgumentException("회원 ID가 제공되지 않았습니다.");
        }
        if (memberEditDTO.getName() == null || memberEditDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("회원 이름이 유효하지 않습니다.");
        }
        if (memberEditDTO.getNickname() == null || memberEditDTO.getNickname().isEmpty()) {
            throw new IllegalArgumentException("회원 닉네임이 유효하지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public boolean validatePassword(String nickname, String password) {
        Optional<Member> member = memberRepository.findByNicknameAndDeletedFalse(nickname);

        if (member.isPresent()) {
            String passwordHash = member.get().getPasswordHash();
            return passwordEncoder.matches(password, passwordHash);
        }
        return false;
    }

    @Transactional
    public boolean removeMember(String nickname, String password) {
        Member member = memberRepository.findByNicknameAndDeletedFalse(nickname)
                .orElseThrow(() -> new IllegalArgumentException("닉네임이 존재하지 않습니다."));

        if (passwordEncoder.matches(password, member.getPasswordHash())) {
            memberRepository.softDeleteById(member.getId());
            return true;
        }
        return false;
    }
}
