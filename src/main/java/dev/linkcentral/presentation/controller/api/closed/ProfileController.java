package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.presentation.request.profile.ProfileDetailsRequest;
import dev.linkcentral.presentation.response.profile.ProfileInfoResponse;
import dev.linkcentral.presentation.response.profile.ProfileUpdatedResponse;
import dev.linkcentral.service.dto.profile.ProfileUpdateDTO;
import dev.linkcentral.service.facade.ProfileFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileFacade profileFacade;

    /**
     * 현재 인증된 사용자의 프로필 정보를 가져옵니다.
     *
     * @return 프로필 정보 응답
     */
    @GetMapping("/auth/member-info")
    public ResponseEntity<ProfileInfoResponse> getProfileInfo() {
        MemberCurrentDTO memberCurrentDTO = profileFacade.getUserInfo();
        ProfileInfoResponse response = ProfileInfoResponse.toProfileInfoResponse(memberCurrentDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 프로필을 업데이트합니다.
     *
     * @param profileDetailsRequest 프로필 업데이트 요청
     * @param image 프로필 이미지 파일 (선택 사항)
     * @return 프로필 업데이트 응답
     */
    @PostMapping("/update")
    public ResponseEntity<ProfileUpdatedResponse> updateProfile(
                                 @Validated @ModelAttribute ProfileDetailsRequest profileDetailsRequest,
                                 @RequestParam(value = "image", required = false) MultipartFile image) {

        ProfileUpdateDTO profileUpdateDTO = ProfileDetailsRequest.toUpdateProfileCommand(profileDetailsRequest);
        profileFacade.updateProfile(profileUpdateDTO, image);
        ProfileUpdatedResponse response = ProfileUpdatedResponse.toProfileUpdateResponse(profileUpdateDTO.getMemberId());
        return ResponseEntity.ok(response);
    }
}
