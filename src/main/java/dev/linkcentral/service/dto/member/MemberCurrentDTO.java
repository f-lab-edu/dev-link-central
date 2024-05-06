package dev.linkcentral.service.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberCurrentDTO {

    private Long memberId;
    private String nickname;

}
