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
}
