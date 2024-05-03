package dev.linkcentral.presentation.response.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateResponse {

    private String message;
    private Long memberId;

    public static ProfileUpdateResponse toProfileUpdateResponse(Long memberId) {
        return ProfileUpdateResponse.builder()
                .message("프로필이 성공적으로 업데이트되었습니다.")
                .memberId(memberId)
                .build();
    }
}
