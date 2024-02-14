package dev.linkcentral.service.dto.request;

import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginRequestDTO {

    private String email;
    private String password;
}