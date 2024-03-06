package dev.linkcentral.presentation.controller.view;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.ProfileService;
import dev.linkcentral.service.dto.request.ProfileRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityNotFoundException;

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

        try {
            ProfileRequestDTO profile = profileService.getProfile(memberId);
            model.addAttribute("profile", profile);

            Member member = memberService.getMemberById(memberId);
            if (member != null) {
                model.addAttribute("loggedInUserName", member.getName());
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
}