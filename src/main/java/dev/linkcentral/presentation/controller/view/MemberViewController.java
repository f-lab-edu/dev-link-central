package dev.linkcentral.presentation.controller.view;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.MemberService;
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

    private final MemberService memberService;

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
        Member member = memberService.getCurrentMember();
        if (member == null) {
            return "redirect:/login";
        }
        model.addAttribute("member", member);
        return "/members/edit";
    }

    @GetMapping("/delete-page")
    public String logout() {
        return "/members/delete";
    }
}