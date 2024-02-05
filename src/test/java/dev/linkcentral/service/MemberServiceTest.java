package dev.linkcentral.service;

import dev.linkcentral.common.exception.DuplicateEmailException;
import dev.linkcentral.common.exception.DuplicateNicknameException;
import dev.linkcentral.common.exception.MemberEmailNotFoundException;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.repository.MemberRepository;
import dev.linkcentral.service.dto.request.MemberEditRequestDTO;
import dev.linkcentral.service.dto.request.MemberSaveRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    private Member createTestMember() {
        Member member = Member.builder()
                .name("minseok")
                .passwordHash(passwordEncoder.encode("1234"))
                .email("test@naver.com")
                .nickname("apple")
                .role("USER")
                .deleted(false)
                .build();
        return member;
    }

    private MemberSaveRequestDTO createTestMemberSaveDTO() {
        MemberSaveRequestDTO memberDTO = new MemberSaveRequestDTO();
        memberDTO.setName("minseok");
        memberDTO.setPassword("1234");
        memberDTO.setEmail("test@naver.com");
        memberDTO.setNickname("apple");
        return memberDTO;
    }

    @DisplayName("회원 가입시 데이터베이스 저장 검증")
    @Test
    void save_member_and_verify_database() {
        // given
        MemberSaveRequestDTO memberDTO = createTestMemberSaveDTO();
        Member mockMember = createTestMember();
        when(memberRepository.save(any(Member.class))).thenReturn(mockMember);

        // when
        Member savedMember = memberService.joinMember(memberDTO);

        // then
        assertEquals(mockMember.getId(), savedMember.getId());
        assertEquals(mockMember.getName(), savedMember.getName());
        assertEquals(mockMember.getEmail(), savedMember.getEmail());
        assertEquals(mockMember.getNickname(), savedMember.getNickname());
    }

    @DisplayName("회원 가입시 사용자 등급 USER로 설정 검증")
    @Test
    void sign_up_with_user_role() {
        // given
        MemberSaveRequestDTO memberDTO = createTestMemberSaveDTO();
        Member savedMember = createTestMember();

        // when
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);
        Member result = memberService.joinMember(memberDTO);

        // then
        assertEquals("USER", result.getRole());
    }

    @DisplayName("회원 가입 시, 이메일 중복시 예외 발생 검증")
    @Test
    void register_with_duplicate_email_exception() {
        // given
        MemberSaveRequestDTO newMemberDTO = createTestMemberSaveDTO();

        when(memberRepository.countByEmailIgnoringDeleted(newMemberDTO.getEmail()))
                .thenReturn(1L);

        // when & then
        assertThrows(DuplicateEmailException.class, () -> {
            memberService.joinMember(newMemberDTO);
        });
    }

    @DisplayName("회원 가입 시, 닉네임 중복시 예외 발생 검증")
    @Test
    void register_with_duplicate_nickname_exception() {
        // given
        MemberSaveRequestDTO newMemberDTO = createTestMemberSaveDTO();

        when(memberRepository.existsByNicknameAndDeletedFalse(newMemberDTO.getNickname()))
                .thenReturn(true);

        // when & then
        assertThrows(DuplicateNicknameException.class, () -> {
            memberService.joinMember(newMemberDTO);
        });
    }

    @DisplayName("로그인 성공 검증")
    @Test
    void verify_login_success() {
        // given
        String email = "test@naver.com";
        String password = "1234";
        Member member = createTestMember();

        when(memberRepository.findByEmailAndDeletedFalse(email))
                .thenReturn(Optional.of(member));

        when(passwordEncoder.matches(password, member.getPasswordHash()))
                .thenReturn(true);

        // when
        Optional<Member> loginMember = memberService.loginMember(email, password);

        // then
        assertTrue(loginMember.isPresent());
        assertEquals(email, loginMember.get().getEmail());
    }

    @DisplayName("암호화된 패스워드와 평문 패스워드가 일치 검증")
    @Test
    void verify_password_encryption_match() {
        // given
        String plainPassword = "1234";
        String encodedPassword = passwordEncoder.encode(plainPassword);

        when(passwordEncoder.matches(plainPassword, encodedPassword))
                .thenReturn(true);

        // when & then
        assertTrue(passwordEncoder.matches(plainPassword, encodedPassword));
    }

    @DisplayName("닉네임 중복시 예외 발생 검증")
    @Test
    void user_duplicate_nickname_exception() {
        // given
        String nickname = "apple";

        when(memberRepository.existsByNicknameAndDeletedFalse(nickname))
                .thenReturn(true);

        // when & then
        assertThrows(DuplicateNicknameException.class, () -> {
            memberService.isNicknameDuplicated(nickname);
        });
    }

    @DisplayName("이메일과 이름이 불일치시 예외 발생 검증")
    @Test
    void username_email_mismatch_exception() {
        // given
        String inputEmail = "alstjr@naver.com";
        String inputName = "heedo";

        when(memberRepository.findByEmailAndNameAndDeletedFalse(inputEmail, inputName))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(MemberEmailNotFoundException.class, () -> {
            memberService.userEmailCheck(inputEmail, inputName);
        });
    }

    @DisplayName("회원 정보 수정 후 데이터베이스 저장 검증")
    @Test
    void modify_member_information_and_verify() {
        // given
        Member originalMember = createTestMember();
        MemberEditRequestDTO memberEditDTO = new MemberEditRequestDTO(
                originalMember.getId(),
                "heedo",
                "7777",
                "mango"
        );

        when(memberRepository.findById(originalMember.getId()))
                .thenReturn(Optional.of(originalMember));

        when(passwordEncoder.encode("7777"))
                .thenReturn("encodedNewPassword");

        // when
        memberService.updateMember(memberEditDTO);

        // then
        assertEquals("heedo", originalMember.getName());
        assertEquals("mango", originalMember.getNickname());
        assertEquals("encodedNewPassword", originalMember.getPasswordHash());
    }

    @DisplayName("로그인 되어 있는 유저 패스워드와 입력받은 비밀번호 일치 여부 검증")
    @Test
    void verify_current_password_match() {
        // given
        String nickname = "apple";
        String correctPassword = "1234";
        String incorrectPassword = "wrongPassword";
        Member testMember = createTestMember();

        when(memberRepository.findByNicknameAndDeletedFalse(nickname))
                .thenReturn(Optional.of(testMember));

        when(passwordEncoder.matches(correctPassword, testMember.getPasswordHash()))
                .thenReturn(true);

        when(passwordEncoder.matches(incorrectPassword, testMember.getPasswordHash()))
                .thenReturn(false);

        // when
        boolean matchCorrectPassword = memberService.checkPassword(nickname, correctPassword);
        boolean matchIncorrectPassword = memberService.checkPassword(nickname, incorrectPassword);

        // then
        assertTrue(matchCorrectPassword);
        assertFalse(matchIncorrectPassword);
    }

    @DisplayName("유저 소프트 삭제 검증")
    @Test
    void verify_user_soft_deletion() {
        // given
        String nickname = "apple";
        String correctPassword = "1234";
        Member testMember = createTestMember();

        when(memberRepository.findByNicknameAndDeletedFalse(nickname))
                .thenReturn(Optional.of(testMember));

        when(passwordEncoder.matches(correctPassword, testMember.getPasswordHash()))
                .thenReturn(true);

        // when
        boolean isDeleted = memberService.deleteMember(nickname, correctPassword);

        // then
        assertTrue(isDeleted);
    }
}