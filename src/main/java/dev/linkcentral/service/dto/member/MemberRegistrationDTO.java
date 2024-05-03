package dev.linkcentral.service.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberRegistrationDTO {

    private String name;
    private String email;
    private String password;
    private String nickname;
    private List<String> roles;

}
