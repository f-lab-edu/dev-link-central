package dev.linkcentral.presentation.response.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDeletedResponse {

    private boolean success;
    private String message;

    public static MemberDeletedResponse toSoftDeleteMemberResponse(boolean softDeleteMember) {
        if (softDeleteMember) {
            return new MemberDeletedResponse(true, "회원 탈퇴가 완료되었습니다.");
        }
        return new MemberDeletedResponse(false, "회원 탈퇴 처리 중 오류가 발생했습니다.");
    }
}
