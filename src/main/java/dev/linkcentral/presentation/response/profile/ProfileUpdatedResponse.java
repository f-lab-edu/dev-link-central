package dev.linkcentral.presentation.response.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdatedResponse {

    private String message;
    private Long memberId;

    public static ProfileUpdatedResponse toProfileUpdateResponse(Long memberId) {
        return ProfileUpdatedResponse.builder()
                .message("프로필이 성공적으로 업데이트되었습니다.")
                .memberId(memberId)
                .build();
    }
}
