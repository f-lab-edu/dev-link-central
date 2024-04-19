package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.ProfileService;

import dev.linkcentral.presentation.dto.request.profile.ProfileRequest;
import dev.linkcentral.presentation.dto.response.profile.ProfileUpdateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final MemberService memberService;
    private final ProfileService profileService;

    @GetMapping("/auth/member-info")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Member member = memberService.getCurrentMember();
        if (member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
        return ResponseEntity.ok(Collections.singletonMap("memberId", member.getId()));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(@ModelAttribute ProfileRequest profileDTO,
                                           @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            Member member = memberService.getCurrentMember();
            profileService.updateProfile(profileDTO, image);
            return ResponseEntity.ok(
                    ProfileUpdateResponse.builder()
                            .message("프로필이 성공적으로 업데이트되었습니다.")
                            .memberId(profileDTO.getMemberId())
                            .build()
            );
        } catch (Exception e) {
            log.error("프로필 업데이트 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ProfileUpdateResponse.builder()
                            .message("프로필 업데이트에 실패했습니다.")
                            .build()
            );
        }
    }
}