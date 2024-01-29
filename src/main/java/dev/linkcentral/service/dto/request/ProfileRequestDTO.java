package dev.linkcentral.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequestDTO {

    private Long memberId;
    private String bio;
    private String imageUrl;
}