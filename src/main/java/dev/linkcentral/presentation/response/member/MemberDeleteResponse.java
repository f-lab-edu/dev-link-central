package dev.linkcentral.presentation.response.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDeleteResponse {

    private boolean success;
    private String message;
}
