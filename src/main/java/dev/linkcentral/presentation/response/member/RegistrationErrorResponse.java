package dev.linkcentral.presentation.response.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationErrorResponse {

    private boolean success = false; // 오류 응답이므로 기본적으로 false
    private String message;
    private String details;

    public RegistrationErrorResponse(String message) {
        this.message = message;
    }

    public RegistrationErrorResponse(String message, String details) {
        this.message = message;
        this.details = details;
    }

}
