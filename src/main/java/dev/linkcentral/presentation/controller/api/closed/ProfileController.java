package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.presentation.request.profile.ProfileDetailsRequest;
import dev.linkcentral.presentation.response.profile.ProfileInfoResponse;
import dev.linkcentral.presentation.response.profile.ProfileUpdateResponse;
import dev.linkcentral.service.dto.member.MemberCurrentDTO;
import dev.linkcentral.service.dto.profile.ProfileUpdateDTO;
import dev.linkcentral.service.facade.ProfileFacade;
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

    private final ProfileFacade profileFacade;
    private final ProfileMapper profileMapper;

    @GetMapping("/auth/member-info")
    public ResponseEntity<ProfileInfoResponse> getUserInfo() {
        MemberCurrentDTO memberCurrentDTO = profileFacade.getUserInfo();
        ProfileInfoResponse response = profileMapper.toProfileInfoResponse(memberCurrentDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<ProfileUpdateResponse> updateProfile(@ModelAttribute ProfileDetailsRequest profileDetailsRequest,
                                                    @RequestParam(value = "image", required = false) MultipartFile image) {

        ProfileUpdateDTO profileUpdateDTO = profileMapper.toUpdateProfileCommand(profileDetailsRequest);
        profileFacade.updateProfile(profileUpdateDTO, image);
        ProfileUpdateResponse response = profileMapper.profileUpdatedResponse(profileUpdateDTO.getMemberId());
        return ResponseEntity.ok(response);
    }
}