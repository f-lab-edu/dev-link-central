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
}
