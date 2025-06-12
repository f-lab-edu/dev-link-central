package dev.linkcentral.presentation.request.member;

import dev.linkcentral.service.dto.member.MemberLoginRequestDTO;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginRequest {

    @NotBlank(message = "이메일 주소는 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 주소 형식이 아닙니다.")
    @Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "올바른 이메일 주소를 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 4, max = 20, message = "비밀번호는 4자 이상 20자 이하로 입력해 주세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$", message = "비밀번호는 최소 하나의 영문자와 숫자를 포함해야 합니다.")
    private String password;

    public static MemberLoginRequestDTO toMemberLoginRequestCommand(MemberLoginRequest memberLoginRequest) {
        return new MemberLoginRequestDTO(
                memberLoginRequest.getEmail(),
                memberLoginRequest.getPassword());
    }
}
