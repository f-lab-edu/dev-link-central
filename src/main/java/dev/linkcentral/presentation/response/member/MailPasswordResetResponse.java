package dev.linkcentral.presentation.response.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailPasswordResetResponse {

    private boolean success;
    private String message;

    public static MailPasswordResetResponse toMailPasswordResetResponse() {
        return new MailPasswordResetResponse(true, "임시 비밀번호가 이메일로 발송되었습니다.");
    }
}
