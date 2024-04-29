package dev.linkcentral.service.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    private Long id;
    private String name;
    private String email;
    private String nickname;
}
