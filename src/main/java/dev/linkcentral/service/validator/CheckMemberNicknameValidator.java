package dev.linkcentral.service.validator;

import dev.linkcentral.database.repository.MemberRepository;
import dev.linkcentral.service.dto.MemberSaveRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@RequiredArgsConstructor
@Component
public class CheckMemberNicknameValidator extends MemberAbstractValidator<MemberSaveRequestDTO>{

    private final MemberRepository memberRepository;

    @Override
    protected void doValidate(MemberSaveRequestDTO dto, Errors errors) {
        // 중복인 경우
        if (memberRepository.existsByNickname(dto.toEntity().getNickname())) {
            errors.rejectValue("username","닉네임 중복 오류", "이미 사용 중인 닉네임 입니다.");
        }
    }
}