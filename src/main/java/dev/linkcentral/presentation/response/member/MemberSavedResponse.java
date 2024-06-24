package dev.linkcentral.presentation.response.member;

import dev.linkcentral.service.dto.member.MemberRegistrationResultDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberSavedResponse {

    private Long id;
    private String message;

    public static MemberSavedResponse toMemberSaveResponse(MemberRegistrationResultDTO registrationResultDTO) {
        return new MemberSavedResponse(registrationResultDTO.getMemberId(), "회원 등록 성공");
    }
}
