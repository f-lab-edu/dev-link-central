package dev.linkcentral.presentation.request.member;

import dev.linkcentral.service.dto.member.MemberEditDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberEditRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long id;

    @NotBlank(message = "사용자 이름은 필수입니다.")
    @Size(min = 1, max = 100, message = "사용자 이름은 2자 이상 100자 이하이어야 합니다.")
    private String name;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 4, max = 50, message = "비밀번호는 4자 이상 50자 이하이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d).+$", message = "비밀번호는 최소 하나의 소문자와 숫자를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "사용자은 필수입니다.")
    @Size(min = 1, max = 50, message = "별명은 2자 이상 50자 이하이어야 합니다.")
    private String nickname;

    public static MemberEditDTO toMemberEditCommand(MemberEditRequest request) {
        return MemberEditDTO.builder()
                .id(request.getId())
                .name(request.getName())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .build();
    }
}
