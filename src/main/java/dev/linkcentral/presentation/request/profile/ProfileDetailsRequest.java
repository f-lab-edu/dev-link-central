package dev.linkcentral.presentation.request.profile;

import dev.linkcentral.service.dto.profile.ProfileUpdateDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "사용자 프로필 세부 정보를 업데이트하기 위한 요청 데이터")
public class ProfileDetailsRequest {

    @ApiModelProperty(value = "사용자의 고유 식별자", required = true)
    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long memberId;

    @ApiModelProperty(value = "사용자의 자기소개")
    @Size(max = 500, message = "자기소개는 최대 500자까지 입력 가능합니다.")
    private String bio;

    @ApiModelProperty(value = "프로필 이미지 URL")
    private String imageUrl;

    public static ProfileUpdateDTO toUpdateProfileCommand(ProfileDetailsRequest profileDetailsRequest) {
        return new ProfileUpdateDTO(
                profileDetailsRequest.getMemberId(),
                profileDetailsRequest.getBio(),
                profileDetailsRequest.getImageUrl());
    }
}
