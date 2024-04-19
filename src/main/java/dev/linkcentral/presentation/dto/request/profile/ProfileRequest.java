package dev.linkcentral.presentation.dto.request.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO: request, response는 service package가 아닌 presentation layer
// TODO: request,response를 dto로 변환해서 service layer로 전달
/**
 * ProfileRequest(presentation) ->
 * ProfileDto(service) ->
 * Profile(entity) ->
 * ProfileDto(service) ->
 * ProfileResponse(presentation)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {

    private Long memberId;
    private String bio;
    private String imageUrl;
}