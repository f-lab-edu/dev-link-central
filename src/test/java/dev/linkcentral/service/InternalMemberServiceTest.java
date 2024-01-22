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
class InternalMemberServiceTest {

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

    @DisplayName("회원 가입이 진행되고, DB에 데이터가 제대로 저장되었는지 검사한다.")
    @Test
    void verify_user_data_saved_on_sign_up() {
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

    @DisplayName("회원 가입 시, 사용자(=USER)로 등급으로 등록되는지 검사한다.")
    @Test
    void verify_user_grade_on_sign_up() {
        // given
        MemberSaveRequestDTO memberDTO = createTestMemberSaveRequestDTO();

        // when
        Member savedMember = memberService.joinMember(memberDTO);

        // then
        assertEquals("USER", savedMember.getRole());
    }

    @DisplayName("회원 가입 시, 중복된 이메일을 입력하면 예외 처리한다.")
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

    @DisplayName("회원 가입 시, 중복된 닉네임을 입력하면 예외 처리한다.")
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

    @DisplayName("로그인이 성공적으로 되는지 검사한다.")
    @Test
    void check_login_success() {
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

    @DisplayName("암호화된 패스워드와 평문 패스워드가 일치한지 검사한다.")
    @Test
    void check_password_encryption_match() {
        // given
        String password = "1234";
        String encryptedPassword = passwordEncoder.encode(password);

        // when & then
        assertTrue(passwordEncoder.matches(password, encryptedPassword));
    }

    @DisplayName("DB에 중복된 닉네임이 있다면, 예외 처리한다.")
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

    @DisplayName("[비밀번호 찾기] DB에 저장된 유저의 이름과 이메일이 일치하지 않을 경우 예외 처리한다.")
    @Test
    void user_name_email_mismatch_exception() {
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

    @DisplayName("회원 정보를 수정하고 DB에 저장된 데이터를 검사한다.")
    @Test
    void modify_member_information() {
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

    @DisplayName("로그인 되어 있는 패스워드와 현재 사용중인 유저의 비밀번호를 입력받아 일치하는지 검사한다.")
    @Test
    void check_password_match() {
        // given
        String inputNickname = "apple";
        String inputPassword = "wrongPassword";

        Member originalMember = createTestMember();
        memberRepository.save(originalMember);

        // when & then
        assertFalse(memberService.checkPassword(inputNickname, inputPassword));
    }

    @DisplayName("유저를 삭제할 경우, 소프트 삭제로 이루어졌는지 검사한다.")
    @Test
    void user_soft_delete_check() {
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