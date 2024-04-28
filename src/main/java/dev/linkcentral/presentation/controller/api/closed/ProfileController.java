package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.dto.ProfileUpdateDTO;
import dev.linkcentral.presentation.request.profile.ProfileDetailsRequest;
import dev.linkcentral.presentation.response.profile.ProfileInfoResponse;
import dev.linkcentral.presentation.response.profile.ProfileUpdateResponse;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.ProfileService;
import dev.linkcentral.service.mapper.ProfileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final MemberService memberService;
    private final ProfileService profileService;
    private final ProfileMapper profileMapper;

    @GetMapping("/auth/member-info")
    public ResponseEntity<ProfileInfoResponse> getUserInfo() {
        Member member = memberService.getCurrentMember();
        ProfileInfoResponse response = profileMapper.toProfileInfoResponse(member);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<ProfileUpdateResponse> updateProfile(@ModelAttribute ProfileDetailsRequest profileDetailsRequest,
                                                               @RequestParam(value = "image", required = false) MultipartFile image) {
        Member member = memberService.getCurrentMember();
        ProfileUpdateDTO profileUpdateDTO = profileMapper.toProfileUpdateDTO(profileDetailsRequest);
        profileService.updateProfile(profileUpdateDTO, image);
        return profileMapper.profileUpdatedResponse(profileDetailsRequest.getMemberId());
    }
}