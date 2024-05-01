package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.presentation.request.profile.ProfileDetailsRequest;
import dev.linkcentral.presentation.response.profile.ProfileInfoResponse;
import dev.linkcentral.presentation.response.profile.ProfileUpdateResponse;
import dev.linkcentral.service.dto.member.MemberCurrentDTO;
import dev.linkcentral.service.dto.profile.ProfileUpdateDTO;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public ProfileInfoResponse toProfileInfoResponse(MemberCurrentDTO memberCurrentDTO) {
        if (memberCurrentDTO.getMember() == null) {
            return null;
        }
        return ProfileInfoResponse.builder()
                .memberId(memberCurrentDTO.getMember().getId())
                .build();
    }

    public ProfileUpdateResponse createProfileUpdateResponse(Long memberId) {
        return ProfileUpdateResponse.builder()
                .message("프로필이 성공적으로 업데이트되었습니다.")
                .memberId(memberId)
                .build();
    }

    public ProfileUpdateDTO toProfileUpdateDTO(ProfileUpdateDTO profileDetailsRequest) {
        return ProfileUpdateDTO.builder()
                .memberId(profileDetailsRequest.getMemberId())
                .bio(profileDetailsRequest.getBio())
                .imageUrl(profileDetailsRequest.getImageUrl())
                .build();
    }

    public MemberCurrentDTO toMemberCurrentDTO(Member currentMember) {
        return new MemberCurrentDTO(currentMember);
    }

    public ProfileUpdateDTO toUpdateProfileCommand(ProfileDetailsRequest profileDetailsRequest) {
        return new ProfileUpdateDTO(
                profileDetailsRequest.getMemberId(),
                profileDetailsRequest.getBio(),
                profileDetailsRequest.getImageUrl());
    }

}
