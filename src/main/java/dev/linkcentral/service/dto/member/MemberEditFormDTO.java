package dev.linkcentral.service.dto.member;

import dev.linkcentral.database.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberEditFormDTO {

    private Member member;

}
