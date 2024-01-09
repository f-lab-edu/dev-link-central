package dev.linkcentral.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberEditRequestDTO {

    private Long id;
    private String name;
    private String password;
    private String nickname;

}