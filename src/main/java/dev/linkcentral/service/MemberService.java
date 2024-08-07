package dev.linkcentral.service;

import dev.linkcentral.common.exception.DuplicateEmailException;
import dev.linkcentral.common.exception.DuplicateNicknameException;
import dev.linkcentral.common.exception.MemberEmailNotFoundException;
import dev.linkcentral.common.exception.MemberRegistrationException;
import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.entity.member.MemberStatus;
import dev.linkcentral.database.repository.member.MemberRepository;
import dev.linkcentral.infrastructure.jwt.JwtTokenDTO;
import dev.linkcentral.infrastructure.jwt.TokenProvider;
import dev.linkcentral.service.dto.member.MemberEditDTO;
import dev.linkcentral.service.dto.member.MemberInfoDTO;
import dev.linkcentral.service.dto.member.MemberMailDTO;
import dev.linkcentral.service.dto.member.MemberRegistrationDTO;
import dev.linkcentral.service.mapper.MemberMapper;
import dev.linkcentral.service.security.SecurityUtils;
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

    private final MemberMapper memberMapper;
    private final JavaMailSender mailSender;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    /**
     * 현재 사용자의 정보를 가져온다
     *
     * @return 현재 사용자의 정보 DTO
     */
    public MemberInfoDTO getCurrentUserInfo() {
        Member member = getCurrentMember();
        return memberMapper.toMemberInfoDTO(member);
    }

    /**
     * 이메일과 삭제되지 않은 상태를 기준으로 회원을 찾는다
     *
     * @param email 이메일
     * @return 회원 엔티티
     */
    @Transactional(readOnly = true)
    public Member findByEmailAndDeletedFalse(String email) {
        return memberRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 유저를 찾을 수 없습니다."));
    }

    /**
     * ID로 회원을 찾는다
     *
     * @param memberId 회원 ID
     * @return 회원 엔티티
     */
    @Transactional(readOnly = true)
    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 멤버를 찾을 수 없습니다."));
    }

    /**
     * ID로 회원을 가져온다
     *
     * @param memberId 회원 ID
     * @return 회원 엔티티
     */
    @Transactional(readOnly = true)
    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("ID가 있는 회원을 찾을 수 없습니다."));
    }

    /**
     * 현재 인증된 회원을 가져온다
     *
     * @return 회원 엔티티
     */
    @Transactional(readOnly = true)
    public Member getCurrentMember() {
        String email = SecurityUtils.getCurrentUserUsername();
        return memberRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
    }

    /**
     * 현재 인증된 회원을 가져옵니다. 인증되지 않은 경우 null을 반환
     *
     * @return 회원 엔티티 또는 null
     */
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

    /**
     * 인증하고 JWT 토큰을 생성
     *
     * @param username 사용자 이름
     * @param password 비밀번호
     * @return JWT 토큰 DTO
     */
    @Transactional
    public JwtTokenDTO authenticateAndGenerateJwtToken(String username, String password) {
        Member member = findMemberByEmail(username);

        if (!passwordEncoder.matches(password, member.getPasswordHash())) {
            throw new IllegalArgumentException("아이디 혹은 비밀번호가 일치하지 않습니다.");
        }

        Collection<? extends GrantedAuthority> authorities = member.getAuthorities();
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password, authorities);
        return tokenProvider.generateToken(authentication);
    }

    /**
     * 회원을 등록
     *
     * @param memberDTO 회원 등록 DTO
     * @return 등록된 회원 엔티티
     */
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

    /**
     * 닉네임 중복을 검사
     *
     * @param nickname 닉네임
     * @return 중복 여부
     */
    @Transactional(readOnly = true)
    public boolean validateNicknameDuplication(String nickname) {
        boolean isDuplicated = memberRepository.existsByNicknameAndDeletedFalse(nickname);

        if (isDuplicated) {
            throw new DuplicateNicknameException("닉네임이 중복되었습니다.: " + nickname);
        }
        return isDuplicated;
    }

    /**
     * 유저 이메일의 유효성을 검사
     *
     * @param userEmail 유저 이메일
     * @return 유효 여부
     */
    @Transactional(readOnly = true)
    public boolean validateUserEmail(String userEmail) {
        Optional<Member> member = memberRepository.findByEmailAndDeletedFalse(userEmail);

        if (member.isEmpty()) {
            throw new MemberEmailNotFoundException("유저의 이메일을 찾을 수 없습니다.");
        }
        return true;
    }

    /**
     * 비밀번호 재설정을 위한 메일을 생성
     *
     * @param userEmail 유저 이메일
     * @return 생성된 메일 DTO
     */
    @Transactional
    public MemberMailDTO createMailForPasswordReset(String userEmail) {
        String generatedPassword = createTemporaryPassword();

        MemberMailDTO memberMailDTO = MemberMailDTO.builder()
                .address(userEmail)
                .title("임시비밀번호 안내 이메일 입니다.")
                .message("안녕하세요. 회원님의 임시비밀번호 안내 관련 이메일 입니다. " +
                        "임시 비밀번호는 " + generatedPassword + " 입니다.")
                .build();

        resetPassword(generatedPassword, userEmail);
        return memberMailDTO;
    }

    /**
     * 비밀번호를 재설정
     *
     * @param generatedPassword 생성된 비밀번호
     * @param userEmail         유저 이메일
     */
    @Transactional
    public void resetPassword(String generatedPassword, String userEmail) {
        String passwordHash = passwordEncoder.encode(generatedPassword);
        updateMemberPassword(userEmail, passwordHash);
    }

    /**
     * 이메일을 보낸다
     *
     * @param memberMailDTO 메일 DTO
     */
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

    /**
     * 회원 정보를 수정
     *
     * @param memberEditDTO 회원 수정 DTO
     */
    @Transactional
    public void editMember(MemberEditDTO memberEditDTO) {
        validateMemberEditDTO(memberEditDTO);

        Member memberEntity = memberRepository.findById(memberEditDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원 찾기 실패"));

        memberMapper.updateMemberFromEditDTO(memberEntity, memberEditDTO);
    }

    /**
     * 닉네임과 비밀번호를 기준으로 비밀번호의 유효성을 검사
     *
     * @param nickname 닉네임
     * @param password 비밀번호
     * @return 유효 여부
     */
    @Transactional(readOnly = true)
    public boolean validatePassword(String nickname, String password) {
        Optional<Member> member = memberRepository.findByNicknameAndDeletedFalse(nickname);

        if (member.isPresent()) {
            String passwordHash = member.get().getPasswordHash();
            return passwordEncoder.matches(password, passwordHash);
        }
        return false;
    }

    /**
     * 회원을 삭제
     *
     * @param nickname 닉네임
     * @param password 비밀번호
     * @return 삭제 여부
     */
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

    /**
     * 주어진 이메일로 회원을 찾는다
     *
     * @param email 이메일
     * @return 회원 엔티티
     */
    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
    }

    /**
     * 회원 등록 시 중복 검사를 수행
     *
     * @param memberDTO 회원 등록 DTO
     * @param nickname  닉네임
     */
    private void validateForDuplicates(MemberRegistrationDTO memberDTO, String nickname) {
        if (memberRepository.existsByNicknameAndDeletedFalse(nickname)) {
            throw new DuplicateNicknameException("닉네임이 이미 사용 중입니다.");
        }

        if (memberRepository.countByEmailIgnoringDeleted(memberDTO.getEmail()) > 0) {
            throw new DuplicateEmailException("중복된 이메일 주소입니다.");
        }
    }

    /**
     * 회원 비밀번호를 업데이트
     *
     * @param userEmail    유저 이메일
     * @param passwordHash 비밀번호 해시
     */
    private void updateMemberPassword(String userEmail, String passwordHash) {
        Member member = memberRepository.findByEmailAndDeletedFalse(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("유저의 이메일을 찾을 수 없습니다."));
        memberRepository.updatePasswordById(member.getId(), passwordHash);
    }

    /**
     * 회원 수정 DTO를 검증
     *
     * @param memberEditDTO 회원 수정 DTO
     */
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

    /**
     * 임시 비밀번호를 생성
     *
     * @return 생성된 임시 비밀번호
     */
    private String createTemporaryPassword() {
        char[] charSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        StringBuilder str = new StringBuilder();

        int idx;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str.append(charSet[idx]);
        }
        return str.toString();
    }
}
