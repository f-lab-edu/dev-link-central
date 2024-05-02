package dev.linkcentral.presentation.request.member;

import dev.linkcentral.service.dto.member.MemberDeleteRequestDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "회원 탈퇴 요청을 위한 데이터")
public class MemberDeleteRequest {

    @ApiModelProperty(value = "회원의 비밀번호", required = true)
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 4, max = 50, message = "비밀번호는 4자 이상 50자 이하이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d).+$", message = "비밀번호는 최소 하나의 소문자와 숫자를 포함해야 합니다.")
    private String password;

    public static MemberDeleteRequestDTO toMemberDeleteRequestCommand(MemberDeleteRequest request) {
        return new MemberDeleteRequestDTO(request.getPassword());
    }
}
