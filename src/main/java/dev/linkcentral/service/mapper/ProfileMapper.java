package dev.linkcentral.service.mapper;

import dev.member.entity.Member;
import dev.linkcentral.database.entity.profile.Profile;
import dev.linkcentral.service.dto.profile.ProfileDetailsDTO;
import dev.linkcentral.service.dto.profile.ProfileUpdateDTO;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public ProfileUpdateDTO toProfileUpdateDTO(ProfileUpdateDTO profileDetailsRequest) {
        return ProfileUpdateDTO.builder()
                .memberId(profileDetailsRequest.getMemberId())
                .bio(profileDetailsRequest.getBio())
                .imageUrl(profileDetailsRequest.getImageUrl())
                .build();
    }

    public MemberCurrentDTO toMemberCurrentDTO(Member currentMember) {
        return MemberCurrentDTO.builder()
                .memberId(currentMember.getId())
                .name(currentMember.getName())
                .build();
    }

    public ProfileDetailsDTO toProfileDetailsDTO(Profile profile) {
        return new ProfileDetailsDTO(
                profile.getMember().getId(),
                profile.getBio(),
                profile.getImageUrl()
        );
    }
}
