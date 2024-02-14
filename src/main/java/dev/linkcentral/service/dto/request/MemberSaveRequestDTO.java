package dev.linkcentral.service.dto.request;

import dev.linkcentral.database.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"name", "password", "email", "nickname", "role"})
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

    private List<String> roles = new ArrayList<>();

    public void updateRole(List<String> roles) {
        this.roles = roles;
    }

    public Member toEntity() {
        return Member.builder()
                .name(name)
                .passwordHash(password)
                .email(email)
                .roles(roles)
                .build();
    }
}