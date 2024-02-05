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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MemberServiceIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Member createTestMember() {
        Member member = Member.builder()
                .name("minseok")
                .passwordHash(passwordEncoder.encode("1234"))
                .email("test@naver.com")
                .nickname("apple")
                .deleted(false)
                .build();
        return member;
    }

    private MemberSaveRequestDTO createTestMemberSaveRequestDTO() {
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
        MemberSaveRequestDTO memberDTO = createTestMemberSaveRequestDTO();

        // when
        Member savedMember = memberService.joinMember(memberDTO);
        Member registeredMember = memberRepository.findById(savedMember.getId()).orElse(null);

        // then
        assertEquals(savedMember.getId(), registeredMember.getId());
        assertEquals(savedMember.getName(), registeredMember.getName());
        assertEquals(savedMember.getEmail(), registeredMember.getEmail());
        assertEquals(savedMember.getNickname(), registeredMember.getNickname());
    }

    @DisplayName("회원 가입시 사용자 등급 USER로 설정 검증")
    @Test
    void sign_up_with_user_role() {
        // given
        MemberSaveRequestDTO memberDTO = createTestMemberSaveRequestDTO();

        // when
        Member savedMember = memberService.joinMember(memberDTO);

        // then
        assertEquals("USER", savedMember.getRole());
    }

    @DisplayName("회원 가입 시, 이메일 중복시 예외 발생 검증")
    @Test
    @Transactional
    void register_with_duplicate_email_exception() {
        // given
        Member existingMember = createTestMember();
        memberRepository.save(existingMember);

        MemberSaveRequestDTO newMemberDTO = new MemberSaveRequestDTO();
        newMemberDTO.setName("heedo");
        newMemberDTO.setEmail("test@naver.com");
        newMemberDTO.setNickname("banana");
        newMemberDTO.setPassword("1234");

        // when & then
        assertThrows(DuplicateEmailException.class, () -> {
            memberService.joinMember(newMemberDTO);
        });
    }

    @DisplayName("회원 가입 시, 닉네임 중복시 예외 발생 검증")
    @Test
    void register_with_duplicate_nickname_exception() {
        // given
        Member existingMember = createTestMember();
        memberRepository.save(existingMember);

        MemberSaveRequestDTO newMemberDTO = new MemberSaveRequestDTO();
        newMemberDTO.setName("heedo");
        newMemberDTO.setEmail("test@naver.com");
        newMemberDTO.setNickname("apple");
        newMemberDTO.setPassword("1234");

        // when & then
        assertThrows(DuplicateNicknameException.class, () -> {
            memberService.joinMember(newMemberDTO);
        });
    }

    @DisplayName("로그인 성공 검증")
    @Test
    void verify_login_success() {
        // given
        String inputEmail = "test@naver.com";
        String inputPassword = "1234";

        Member member = createTestMember();
        memberRepository.save(member);

        // when
        Optional<Member> loggedInMember = memberService.loginMember(inputEmail, inputPassword);

        // then
        assertTrue(loggedInMember.isPresent());
        assertEquals(inputEmail, loggedInMember.get().getEmail());
    }

    @DisplayName("암호화된 패스워드와 평문 패스워드가 일치 검증")
    @Test
    void verify_password_encryption_match() {
        // given
        String password = "1234";
        String encryptedPassword = passwordEncoder.encode(password);

        // when & then
        assertTrue(passwordEncoder.matches(password, encryptedPassword));
    }

    @DisplayName("닉네임 중복시 예외 발생 검증")
    @Test
    void user_duplicate_nickname_exception() {
        // given
        String nickname = "apple";

        Member member = createTestMember();
        memberRepository.save(member);

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

        Member member = createTestMember();
        memberRepository.save(member);

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
        memberRepository.save(originalMember);

        // when
        MemberEditRequestDTO editDTO = new MemberEditRequestDTO(
                originalMember.getId(),
                "heedo",
                "7777",
                "mango");
        memberService.updateMember(editDTO);

        // then
        Member updatedMember = memberRepository.findById(originalMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        assertEquals("heedo", updatedMember.getName());
        assertEquals("mango", updatedMember.getNickname());
        assertTrue(passwordEncoder.matches("7777", updatedMember.getPasswordHash()));
    }

    @DisplayName("로그인 되어 있는 유저 패스워드와 입력받은 비밀번호 일치 여부 검증")
    @Test
    void verify_current_password_match() {
        // given
        String inputNickname = "apple";
        String inputPassword = "wrongPassword";

        Member originalMember = createTestMember();
        memberRepository.save(originalMember);

        // when & then
        assertFalse(memberService.checkPassword(inputNickname, inputPassword));
    }

    @DisplayName("유저 소프트 삭제 검증")
    @Test
    void verify_user_soft_deletion() {
        // given
        String inputPassword = "1234";

        Member testMember = createTestMember();
        memberRepository.saveAndFlush(testMember);

        // When
        memberService.deleteMember(testMember.getNickname(), inputPassword);

        // 캐시 초기화
        // 캐시된 데이터 대신 데이터베이스에서 최신 데이터를 가져오기 위해.
        entityManager.clear();

        // Then: 멤버가 soft delete 되었는지 확인.
        // deleted 필드가 true로 설정되었는지 확인하여, 실제로 소프트 삭제가 이루어졌는지 검증.
        Member deletedMember = memberRepository.findById(testMember.getId()).orElse(null);
        assertTrue(deletedMember.isDeleted());
    }
}