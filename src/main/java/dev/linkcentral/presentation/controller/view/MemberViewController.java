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
    public String Home() {
        return "/home";
    }

    @GetMapping("/login")
    public String login() {
        return "/members/login";
    }

    @GetMapping("/join-form")
    public String joinMember() {
        return "/members/join";
    }

    @GetMapping("reset-password")
    public String mailAndChangePassword() {
        return "/members/reset-password";
    }

    @GetMapping("/edit-form")
    public String memberEditForm(Model model) {
        MemberEditFormDTO memberDTO = memberFacade.memberEditForm();
        if (memberDTO.getMember() == null) {
            return "redirect:/login";
        }
        model.addAttribute("member", memberDTO.getMember());
        return "/members/edit";
    }

    @GetMapping("/delete-page")
    public String logout() {
        return "/members/delete";
    }
}