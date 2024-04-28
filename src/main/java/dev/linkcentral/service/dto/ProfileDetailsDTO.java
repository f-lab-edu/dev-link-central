package dev.linkcentral.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDetailsDTO {

    private Long memberId;
    private String bio;
    private String imageUrl;

}
