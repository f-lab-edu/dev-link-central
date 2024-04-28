package dev.linkcentral.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateDTO {

    private Long memberId;
    private String bio;
    private String imageUrl;

}
