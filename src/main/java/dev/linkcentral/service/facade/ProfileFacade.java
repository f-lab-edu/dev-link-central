package dev.linkcentral.service.facade;

import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.ProfileService;
import dev.linkcentral.service.dto.member.MemberCurrentDTO;
import dev.linkcentral.service.dto.profile.ProfileDetailsDTO;
import dev.linkcentral.service.dto.profile.ProfileUpdateDTO;
import dev.linkcentral.service.mapper.ArticleMapper;
import dev.linkcentral.service.mapper.ProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ProfileFacade {

    private final MemberService memberService;
    private final ProfileService profileService;
    private final ProfileMapper profileMapper;
    private final ArticleMapper articleMapper;

    public MemberCurrentDTO getUserInfo() {
        Member currentMember = memberService.getCurrentMember();
        return articleMapper.toMemberCurrentDTO(currentMember.getId());
    }

    public void updateProfile(ProfileUpdateDTO profileDetailsRequest, MultipartFile image) {
        Member member = memberService.getCurrentMember();
        ProfileUpdateDTO profileUpdateDTO = profileMapper.toProfileUpdateDTO(profileDetailsRequest);
        profileService.updateProfile(profileUpdateDTO, image);
    }

    public ProfileDetailsDTO getProfile(Long memberId) {
        return profileService.getProfile(memberId);
    }

    public MemberCurrentDTO getMemberById(Long memberId) {
        Member member = memberService.getMemberById(memberId);
        return profileMapper.toMemberCurrentDTO(member);
    }

}
