package dev.linkcentral.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LoginSuccessResponse {

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String redirectUrl;
}