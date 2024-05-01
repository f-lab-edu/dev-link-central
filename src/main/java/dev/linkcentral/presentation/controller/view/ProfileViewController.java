package dev.linkcentral.presentation.controller.view;

import dev.linkcentral.service.dto.member.MemberCurrentDTO;
import dev.linkcentral.service.dto.profile.ProfileDetailsDTO;
import dev.linkcentral.service.facade.ProfileFacade;
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

    private final ProfileFacade profileFacade;

    @GetMapping("/view")
    public String viewProfile(@RequestParam(value = "memberId", required = false) Long memberId, Model model) {
        if (memberId == null) {
            model.addAttribute("error", "memberId가 제공되지 않았습니다.");
            return "profile/view";
        }

        ProfileDetailsDTO profile = profileFacade.getProfile(memberId);
        model.addAttribute("profile", profile);

        MemberCurrentDTO memberCurrentDTO = profileFacade.getMemberById(memberId);
        if (memberCurrentDTO.getMember() != null) {
            model.addAttribute("loggedInUserName", memberCurrentDTO.getMember().getName());
            model.addAttribute("viewedMemberId", memberId);
        }
        return "profile/view";
    }

    @GetMapping("/edit")
    public String profileEditForm(@RequestParam Long memberId, Model model) {
        ProfileDetailsDTO profile = profileFacade.getProfile(memberId);
        model.addAttribute("profile", profile);
        return "profile/edit";
    }

}
