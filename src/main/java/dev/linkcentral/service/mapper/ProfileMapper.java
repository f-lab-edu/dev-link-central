package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.service.dto.member.MemberCurrentDTO;
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

}
