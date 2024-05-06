package dev.linkcentral.presentation.response.profile;

import dev.linkcentral.service.dto.member.MemberCurrentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileInfoResponse {

    private Long memberId;

    public static ProfileInfoResponse toProfileInfoResponse(MemberCurrentDTO memberCurrentDTO) {
        return ProfileInfoResponse.builder()
                .memberId(memberCurrentDTO.getMemberId())
                .build();
    }
}
