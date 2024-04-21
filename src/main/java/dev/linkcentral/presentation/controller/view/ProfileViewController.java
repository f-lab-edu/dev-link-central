package dev.linkcentral.presentation.controller.view;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.presentation.dto.ProfileDetailsDTO;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/view/profile")
public class ProfileViewController {

    private final MemberService memberService;
    private final ProfileService profileService;

    @GetMapping("/view")
    public String viewProfile(@RequestParam(value = "memberId", required = false) Long memberId, Model model) {
        if (memberId == null) {
            model.addAttribute("error", "memberId가 제공되지 않았습니다.");
            return "profile/view";
        }
        ProfileDetailsDTO profile = profileService.getProfile(memberId);
        model.addAttribute("profile", profile);

        Member member = memberService.getMemberById(memberId);
        if (member != null) {
            model.addAttribute("loggedInUserName", member.getName());
            model.addAttribute("viewedMemberId", memberId);
        }
        return "profile/view";
    }

    @GetMapping("/edit")
    public String profileEditForm(@RequestParam Long memberId, Model model) {
        ProfileDetailsDTO profile = profileService.getProfile(memberId);
        model.addAttribute("profile", profile);
        return "profile/edit";
    }
}