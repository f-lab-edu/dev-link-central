package dev.linkcentral.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberEditDTO {

    private Long id;
    private String name;
    private String password;
    private String nickname;
}
