package dev.linkcentral.service.dto.member;

import dev.linkcentral.database.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberCurrentDTO {

    private Member member;

}
