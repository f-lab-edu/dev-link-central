package dev.linkcentral.presentation.response.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberPasswordResponse {

    private boolean result;

    public static MemberPasswordResponse toMemberPasswordResponse(boolean pwFindCheck) {
        return new MemberPasswordResponse(pwFindCheck);
    }
}
