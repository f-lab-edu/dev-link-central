package dev.linkcentral.presentation.dto.request.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberEditRequest {

    private Long id;
    private String name;
    private String password;
    private String nickname;

}