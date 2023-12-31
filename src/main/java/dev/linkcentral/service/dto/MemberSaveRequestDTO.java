package dev.linkcentral.service.dto;

import dev.linkcentral.database.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberSaveRequestDTO {

    @Length(max = 50, message = "이름은 50자 이내로 입력해 주세요.")
    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @Range(max = 100, message = "비밀번호는 100자 이내로 입력해 주세요.")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @Length(max = 100, message = "이메일은 100자 이내로 입력해 주세요.")
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 주소를 입력해주세요.")
    private String email;

    @Length(max = 100, message = "닉네임은 100자 이내로 입력해 주세요.")
    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    private String role;

    public void updateRole(String role) {
        this.role = role;
    }

    public void encryptPassword(String BCryptpassword) {
        this.password = BCryptpassword;
    }

    public Member toEntity() {
        return Member.builder()
                .name(name)
                .password(password)
                .email(email)
                .role(role)
                .build();
    }
}