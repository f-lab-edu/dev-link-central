package dev.linkcentral.service.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberEditResponseDTO {

    int status; // HTTP 상태 코드 중 200
}