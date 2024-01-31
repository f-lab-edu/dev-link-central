package dev.linkcentral.presentation;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.ProfileService;
import dev.linkcentral.service.dto.request.ProfileRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/view")
    public String viewProfile(@RequestParam(value = "memberId", required = false) Long memberId, Model model, HttpSession session) {
        if (memberId == null) {
            model.addAttribute("error", "memberId가 제공되지 않았습니다.");
            return "profile/view";
        }

        try {
            ProfileRequestDTO profile = profileService.getProfile(memberId);
            model.addAttribute("profile", profile);

            Member loggedInMember = (Member) session.getAttribute("member");
            if (loggedInMember != null) {
                model.addAttribute("loggedInUserId", loggedInMember.getId());
                model.addAttribute("loggedInUserName", loggedInMember.getName());
                model.addAttribute("viewedMemberId", memberId);
            }
            return "profile/view";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", "해당 memberId에 대한 프로필을 찾을 수 없습니다.");
            return "error";
        }
    }

    @GetMapping("/edit")
    public String profileEditForm(@RequestParam Long memberId, Model model) {
        // 프로필 수정 폼에 필요한 데이터를 가져옴
        ProfileRequestDTO profile = profileService.getProfile(memberId);
        model.addAttribute("profile", profile);
        return "profile/edit";
    }

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
        return "redirect:/api/v1/profile/view?memberId=" + profileDTO.getMemberId();
    }
}