package dev.linkcentral.presentation.response.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberEditResponse {

    private int statusCode;
    private String message;

    public static MemberEditResponse toUpdateMemberResponse() {
        return new MemberEditResponse(200, "업데이트 성공되었습니다.");
    }
}
