package dev.linkcentral.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO: request, response는 service package가 아닌 presentation layer
// TODO: request,response를 dto로 변환해서 service layer로 전달
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequestDTO {

    private Long memberId;
    private String bio;
    private String imageUrl;
}
