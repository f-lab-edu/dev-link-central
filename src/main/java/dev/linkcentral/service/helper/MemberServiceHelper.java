package dev.linkcentral.service.helper;

import dev.linkcentral.common.exception.DuplicateEmailException;
import dev.linkcentral.common.exception.DuplicateNicknameException;
import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.repository.member.MemberRepository;
import dev.linkcentral.service.dto.member.MemberEditDTO;
import dev.linkcentral.service.dto.member.MemberRegistrationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;

@Component
@RequiredArgsConstructor
public class MemberServiceHelper {

    private final MemberRepository memberRepository;

    /**
     * 회원 등록 시 중복된 닉네임과 이메일을 확인합니다.
     *
     * @param memberDTO 회원 등록 DTO
     * @param nickname  닉네임
     */
    public void validateForDuplicates(MemberRegistrationDTO memberDTO, String nickname) {
        if (memberRepository.existsByNicknameAndDeletedFalse(nickname)) {
            throw new DuplicateNicknameException("닉네임이 이미 사용 중입니다.");
        }

        if (memberRepository.countByEmailIgnoringDeleted(memberDTO.getEmail()) > 0) {
            throw new DuplicateEmailException("중복된 이메일 주소입니다.");
        }
    }

    /**
     * 회원 비밀번호를 업데이트합니다.
     *
     * @param userEmail    사용자 이메일
     * @param passwordHash 해싱된 비밀번호
     */
    public void updateMemberPassword(String userEmail, String passwordHash) {
        Member member = memberRepository.findByEmailAndDeletedFalse(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("유저의 이메일을 찾을 수 없습니다."));
        memberRepository.updatePasswordById(member.getId(), passwordHash);
    }

    /**
     * 회원 수정 DTO의 유효성을 검사합니다.
     *
     * @param memberEditDTO 회원 수정 DTO
     */
    public void validateMemberEditDTO(MemberEditDTO memberEditDTO) {
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
     * 임시 비밀번호를 생성합니다.
     *
     * @return String 임시 비밀번호
     */
    public String createTemporaryPassword() {
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
