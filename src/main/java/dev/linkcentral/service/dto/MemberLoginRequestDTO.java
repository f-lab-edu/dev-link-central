package dev.linkcentral.service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginRequestDTO {

    private String name;
    private String password;
}