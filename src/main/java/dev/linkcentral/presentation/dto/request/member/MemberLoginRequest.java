package dev.linkcentral.presentation.dto.request.member;

import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginRequest {

    private String email;
    private String password;
}