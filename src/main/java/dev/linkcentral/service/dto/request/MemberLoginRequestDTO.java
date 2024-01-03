package dev.linkcentral.service.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginRequestDTO {

    private String email;
    private String password;
}