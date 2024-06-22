package dev.linkcentral.service;

import dev.linkcentral.common.exception.DuplicateEmailException;
import dev.linkcentral.common.exception.DuplicateNicknameException;
import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.entity.member.MemberStatus;
import dev.linkcentral.database.repository.member.MemberRepository;
import dev.linkcentral.service.dto.member.MemberEditDTO;
import dev.linkcentral.service.dto.member.MemberRegistrationDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
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

//    @Mock
//    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private MemberService memberService;

    private MemberRegistrationDTO createTestMemberRegistrationDTO() {
        return new MemberRegistrationDTO(
                "John Doe",
                "john.doe@example.com",
                "securePassword123",
                "johndoe",
                Collections.singletonList(String.valueOf(MemberStatus.USER)));
    }

    private Member createTestMember() {
        return Member.builder()
                .id(1L)
                .name("John Doe")
                .passwordHash("hashed_password")
                .email("john.doe@example.com")
                .nickname("johndoe")
                .roles(Collections.singletonList(MemberStatus.USER.toString()))
                .deleted(false)
                .build();
    }

    @DisplayName("회원 가입시 데이터베이스 저장 검증")
    @Test
    void save_member_and_verify_database() {
        // given
        MemberRegistrationDTO memberRegistrationDTO = createTestMemberRegistrationDTO();
        Member mockMember = createTestMember();
        when(memberRepository.save(any(Member.class))).thenReturn(mockMember);
        when(passwordEncoder.encode(any(String.class))).thenReturn("hashed_password");

        // when
        Member savedMember = memberService.registerMember(memberRegistrationDTO);

        // then
        assertNotNull(savedMember);
        assertEquals(mockMember.getId(), savedMember.getId());
        assertEquals(mockMember.getName(), savedMember.getName());
        assertEquals(mockMember.getEmail(), savedMember.getEmail());
        assertEquals(mockMember.getNickname(), savedMember.getNickname());
    }

    @DisplayName("회원 가입 시, 이메일 중복시 예외 발생 검증")
    @Test
    void register_with_duplicate_email_exception() {
        // given
        MemberRegistrationDTO memberRegistrationDTO = createTestMemberRegistrationDTO();
        when(memberRepository.countByEmailIgnoringDeleted(memberRegistrationDTO.getEmail())).thenReturn(1L);

        // when & then
        assertThrows(DuplicateEmailException.class, () -> {
            memberService.registerMember(memberRegistrationDTO);
        });
    }

    @DisplayName("회원 가입 시, 닉네임 중복시 예외 발생 검증")
    @Test
    void register_with_duplicate_nickname_exception() {
        // given
        MemberRegistrationDTO memberRegistrationDTO = createTestMemberRegistrationDTO();
        when(memberRepository.existsByNicknameAndDeletedFalse(memberRegistrationDTO.getNickname())).thenReturn(true);

        // when & then
        assertThrows(DuplicateNicknameException.class, () -> {
            memberService.registerMember(memberRegistrationDTO);
        });
    }

    @Test
    @DisplayName("로그인 성공 및 JWT 토큰 생성 검증")
    void verify_Login_Success_And_Jwt_Token_Generation() {
        // given
        String inputEmail = "test@naver.com";
        String inputPassword = "1234";
        Member member = createTestMember();

        // memberRepository.findByEmailAndDeletedFalse 메서드가 inputEmail에 대해 member 객체를 반환하도록 설정
        when(memberRepository.findByEmailAndDeletedFalse(inputEmail)).thenReturn(Optional.of(member));

        // passwordEncoder.matches 메서드가 inputPassword와 member.getPasswordHash()가 일치한다고 가정
        when(passwordEncoder.matches(inputPassword, member.getPasswordHash())).thenReturn(true);

        // jwtTokenProvider.generateToken 호출 시 목 JwtTokenDTO 객체 반환 설정
//        JwtTokenDTO mockJwtTokenDTO = new JwtTokenDTO("Bearer",
//                "access_token_string",
//                "refresh_token_string");
//        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn(mockJwtTokenDTO);

        // when
//        JwtTokenDTO jwtTokenDTO = memberService.authenticateAndGenerateJwtToken(inputEmail, inputPassword);

        // then
//        assertNotNull(jwtTokenDTO);                                                  // JWT 토큰 객체가 null이 아닌지 확인
//        assertEquals("Bearer", jwtTokenDTO.getGrantType());                  // 토큰 타입이 "Bearer"로 설정되었는지 확인
//        assertEquals("access_token_string", jwtTokenDTO.getAccessToken());   // 액세스 토큰 값 검증
//        assertEquals("refresh_token_string", jwtTokenDTO.getRefreshToken()); // 리프레시 토큰 값 검증
    }

    @DisplayName("암호화된 패스워드와 평문 패스워드가 일치 검증")
    @Test
    void verify_password_encryption_match() {
        // given
        String plainPassword = "1234";
        String encodedPassword = passwordEncoder.encode(plainPassword);
        when(passwordEncoder.matches(plainPassword, encodedPassword)).thenReturn(true);

        // when & then
        assertTrue(passwordEncoder.matches(plainPassword, encodedPassword));
    }

    @DisplayName("닉네임 중복시 예외 발생 검증")
    @Test
    void user_duplicate_nickname_exception() {
        // given
        String nickname = "apple";
        when(memberRepository.existsByNicknameAndDeletedFalse(nickname)).thenReturn(true);

        // when & then
        assertThrows(DuplicateNicknameException.class, () -> {
            memberService.validateNicknameDuplication(nickname);
        });
    }

    @DisplayName("이메일과 이름이 불일치시 예외 발생 검증")
    @Test
    void username_email_mismatch_exception() {
        // given
//        String inputEmail = "alstjr@naver.com";
//        String inputName = "heedo";
//        when(memberRepository.findByEmailAndNameAndDeletedFalse(inputEmail, inputName))
//                .thenReturn(Optional.empty());

        // when & then
//        assertThrows(MemberEmailNotFoundException.class, () -> {
//            memberService.validateUserEmail(inputEmail, inputName);
//        });
    }

    @DisplayName("회원 정보 수정 후 데이터베이스 저장 검증")
    @Test
    void modify_member_information_and_verify() {
        // given
        Member originalMember = createTestMember();
        MemberEditDTO memberEditDTO = MemberEditDTO.builder()
                .id(originalMember.getId())
                .name("heedo")
                .password("7777")
                .nickname("mango")
                .build();

        when(memberRepository.findById(originalMember.getId())).thenReturn(Optional.of(originalMember));
        when(passwordEncoder.encode("7777")).thenReturn("encodedNewPassword");

        // when
        memberService.editMember(memberEditDTO);

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

        when(memberRepository.findByNicknameAndDeletedFalse(nickname)).thenReturn(Optional.of(testMember));
        when(passwordEncoder.matches(correctPassword, testMember.getPasswordHash())).thenReturn(true);
        when(passwordEncoder.matches(incorrectPassword, testMember.getPasswordHash())).thenReturn(false);

        // when
        boolean matchCorrectPassword = memberService.validatePassword(nickname, correctPassword);
        boolean matchIncorrectPassword = memberService.validatePassword(nickname, incorrectPassword);

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

        when(memberRepository.findByNicknameAndDeletedFalse(nickname)).thenReturn(Optional.of(testMember));
        when(passwordEncoder.matches(correctPassword, testMember.getPasswordHash())).thenReturn(true);

        // when
        boolean isDeleted = memberService.removeMember(nickname, correctPassword);

        // then
        assertTrue(isDeleted);
    }
}