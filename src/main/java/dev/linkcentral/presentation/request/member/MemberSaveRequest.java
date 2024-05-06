package dev.linkcentral.presentation.request.member;

import dev.linkcentral.service.dto.member.MemberRegistrationDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "회원 가입을 위한 요청 데이터")
public class MemberSaveRequest {

    @ApiModelProperty(value = "사용자의 이름", required = true)
    @Length(max = 50, message = "이름은 50자 이내로 입력해 주세요.")
    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @ApiModelProperty(value = "사용자의 비밀번호", required = true)
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 4, max = 20, message = "비밀번호는 4~20자리로 입력해 주세요.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d).+$", message = "비밀번호는 최소 하나의 소문자와 숫자를 포함해야 합니다.")
    private String password;

    @ApiModelProperty(value = "사용자의 이메일 주소", required = true)
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 주소 형식이 아닙니다.")
    @Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "올바른 이메일 주소를 입력해주세요.")
    private String email;

    @ApiModelProperty(value = "사용자의 닉네임", required = true)
    @Length(max = 100, message = "닉네임은 100자 이내로 입력해 주세요.")
    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    @ApiModelProperty(value = "사용자의 역할 목록", notes = "사용자에게 할당된 역할의 리스트")
    private List<String> roles = new ArrayList<>();

    public static MemberRegistrationDTO toMemberRegistrationCommand(MemberSaveRequest request) {
        return MemberRegistrationDTO.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .roles(Collections.singletonList("USER"))
                .build();
    }

}
