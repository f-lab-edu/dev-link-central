package dev.linkcentral.presentation;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.ProfileService;
import dev.linkcentral.service.dto.request.ProfileRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/view")
    public String viewProfile(@RequestParam Long memberId, Model model, HttpSession session) {
        ProfileRequestDTO profile = profileService.getProfile(memberId);
        model.addAttribute("profile", profile);

        // 세션에서 사용자 정보 가져오기
        Member member = (Member) session.getAttribute("member");
        if (member != null) {
            model.addAttribute("loginUserName", member.getName());
        }
        return "profile/view";
    }

    @GetMapping("/edit")
    public String profileEditForm(@RequestParam Long memberId, Model model) {
        ProfileRequestDTO profile = profileService.getProfile(memberId);
        model.addAttribute("profile", profile);
        return "profile/edit";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute ProfileRequestDTO profileDTO, HttpSession session) {
        profileService.updateOrCreateProfile(profileDTO);
        return "redirect:/api/v1/profile/view?memberId=" + profileDTO.getMemberId();
    }

}