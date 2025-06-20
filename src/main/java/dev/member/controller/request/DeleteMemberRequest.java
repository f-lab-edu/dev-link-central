package dev.member.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteMemberRequest {

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 4, max = 50, message = "비밀번호는 4자 이상 50자 이하이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d).+$", message = "비밀번호는 최소 하나의 소문자와 숫자를 포함해야 합니다.")
    private String password;
}
