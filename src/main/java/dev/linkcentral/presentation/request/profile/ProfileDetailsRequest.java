package dev.linkcentral.presentation.request.profile;

import dev.linkcentral.service.dto.profile.ProfileUpdateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDetailsRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long memberId;

    @Size(max = 500, message = "자기소개는 최대 500자까지 입력 가능합니다.")
    private String bio;

    private String imageUrl;

    public static ProfileUpdateDTO toUpdateProfileCommand(ProfileDetailsRequest profileDetailsRequest) {
        return new ProfileUpdateDTO(
                profileDetailsRequest.getMemberId(),
                profileDetailsRequest.getBio(),
                profileDetailsRequest.getImageUrl());
    }
}
