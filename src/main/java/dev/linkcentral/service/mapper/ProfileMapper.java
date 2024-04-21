package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.presentation.dto.ProfileUpdateDTO;
import dev.linkcentral.presentation.dto.request.profile.ProfileRequest;
import dev.linkcentral.presentation.dto.response.profile.ProfileInfoResponse;
import dev.linkcentral.presentation.dto.response.profile.ProfileUpdateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public ProfileInfoResponse toProfileInfoResponse(Member member) {
        if (member == null) {
            return null;
        }
        return ProfileInfoResponse.builder()
                .memberId(member.getId())
                .build();
    }

    public ResponseEntity<ProfileUpdateResponse> profileUpdatedResponse(Long memberId) {
        ProfileUpdateResponse response = ProfileUpdateResponse.builder()
                .message("프로필이 성공적으로 업데이트되었습니다.")
                .memberId(memberId)
                .build();
        return ResponseEntity.ok(response);
    }

    public ProfileUpdateDTO toProfileUpdateDTO(ProfileRequest profileRequest) {
        return ProfileUpdateDTO.builder()
                .memberId(profileRequest.getMemberId())
                .bio(profileRequest.getBio())
                .imageUrl(profileRequest.getImageUrl())
                .build();
    }

}
