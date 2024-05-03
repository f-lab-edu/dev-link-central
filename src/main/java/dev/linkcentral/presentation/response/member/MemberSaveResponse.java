package dev.linkcentral.presentation.response.member;

import dev.linkcentral.service.dto.member.MemberRegistrationResultDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberSaveResponse {

    private Long id;
    private String message;

    public static MemberSaveResponse toMemberSaveResponse(MemberRegistrationResultDTO registrationResultDTO) {
        return new MemberSaveResponse(registrationResultDTO.getMemberId(), "회원 등록 성공");
    }
}
