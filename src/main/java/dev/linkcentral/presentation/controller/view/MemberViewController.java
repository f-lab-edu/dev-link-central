package dev.linkcentral.presentation.controller.view;

import dev.linkcentral.service.dto.member.MemberEditFormDTO;
import dev.linkcentral.service.facade.MemberFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/view/member")
public class MemberViewController {

    private final MemberFacade memberFacade;

    @GetMapping("/")
    public String showHomePage() {
        return "/home";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "/members/login";
    }

    @GetMapping("/join-form")
    public String showJoinForm() {
        return "/members/join";
    }

    @GetMapping("reset-password")
    public String showResetPasswordForm() {
        return "/members/reset-password";
    }

    @GetMapping("/edit-form")
    public String showMemberEditForm(Model model) {
        MemberEditFormDTO memberDTO = memberFacade.memberEditForm();
        if (memberDTO.getMember() == null) {
            return "redirect:/login";
        }
        model.addAttribute("member", memberDTO.getMember());
        return "/members/edit";
    }

    @GetMapping("/delete-page")
    public String showDeleteMemberPage() {
        return "/members/delete";
    }
}