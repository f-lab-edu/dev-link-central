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

    @DisplayName("회원 가입이 진행되고, DB에 데이터가 제대로 저장되었는지 검사한다.")
    @Test
    void verify_user_data_saved_on_sign_up() {
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

    @DisplayName("회원 가입 시, 사용자(=USER)로 등급으로 등록되는지 검사한다.")
    @Test
    void verify_user_grade_on_sign_up() {
        // given
        MemberSaveRequestDTO memberDTO = createTestMemberSaveDTO();
        Member savedMember = createTestMember();

        // when
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);
        Member result = memberService.joinMember(memberDTO);

        // then
        assertEquals("USER", result.getRole());
    }

    @DisplayName("회원 가입 시, 중복된 이메일을 입력하면 예외 처리한다.")
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

    @DisplayName("회원 가입 시, 중복된 닉네임을 입력하면 예외 처리한다.")
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

    @DisplayName("로그인이 성공적으로 되는지 검사하는 테스트")
    @Test
    void login_success() {
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

    @DisplayName("암호화된 패스워드와 평문 패스워드가 일치한지 검사한다.")
    @Test
    void check_password_encryption_match() {
        // given
        String plainPassword = "1234";
        String encodedPassword = passwordEncoder.encode(plainPassword);

        when(passwordEncoder.matches(plainPassword, encodedPassword))
                .thenReturn(true);

        // when & then
        assertTrue(passwordEncoder.matches(plainPassword, encodedPassword));
    }

    @DisplayName("DB에 중복된 닉네임이 있다면, 예외 처리한다.")
    @Test
    void check_duplicate_nickname_exception() {
        // given
        String nickname = "apple";

        when(memberRepository.existsByNicknameAndDeletedFalse(nickname))
                .thenReturn(true);

        // when & then
        assertThrows(DuplicateNicknameException.class, () -> {
            memberService.isNicknameDuplicated(nickname);
        });
    }

    @DisplayName("[비밀번호 찾기] DB에 저장된 유저의 이름과 이메일이 일치하지 않을 경우 예외 처리한다.")
    @Test
    void user_name_email_mismatch_exception1() {
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

    @DisplayName("회원 정보를 수정하고 DB에 저장된 데이터를 검사한다.")
    @Test
    void modify_member_information() {
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

    @DisplayName("로그인 되어 있는 패스워드와 현재 사용중인 유저의 비밀번호를 입력받아 일치하는지 검사한다.")
    @Test
    void check_password_match() {
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

    @DisplayName("유저를 삭제할 경우, 소프트 삭제로 이루어졌는지 검사한다.")
    @Test
    void user_soft_delete_check() {
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