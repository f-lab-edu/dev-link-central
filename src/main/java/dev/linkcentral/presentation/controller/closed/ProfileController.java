package dev.linkcentral.presentation.controller.closed;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.ProfileService;

import dev.linkcentral.service.dto.request.ProfileRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final MemberService memberService;
    private final ProfileService profileService;

    @GetMapping("/auth/member-info")
    @ResponseBody
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

    // TODO: API는 하나의 역할만 하기 -> update profile + form redirection을 모두 하지 말고 update profile만 수행
    // TODO: redirection은 jsp page에서 API 결과(성공/실패)에 따라 처리하도록 변경 가능
    @PostMapping("/update")
    public String updateProfile(@ModelAttribute ProfileRequestDTO profileDTO,
                                @RequestParam(value = "image", required = false) MultipartFile image,
                                RedirectAttributes redirectAttributes) {
        // 프로필 업데이트 요청 처리
        try {
            profileService.updateProfile(profileDTO, image);
            redirectAttributes.addFlashAttribute("message", "프로필이 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            log.error("프로필 업데이트 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("message", "프로필 업데이트에 실패했습니다.");
        }
        return "redirect:/api/v1/view/profile/view?memberId=" + profileDTO.getMemberId();
    }
}
