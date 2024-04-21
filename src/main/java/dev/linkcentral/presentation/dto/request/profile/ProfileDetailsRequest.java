package dev.linkcentral.presentation.dto.request.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDetailsRequest {

    private Long memberId;
    private String bio;
    private String imageUrl;
}