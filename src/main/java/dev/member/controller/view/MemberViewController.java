package dev.member.controller.view;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/view/member")
public class MemberViewController {

    @GetMapping("/")
    public String showHomePage() {
        return "/home"; // 로그인 페이지
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "/members/login"; // 로그인 이후 페이지
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
    public String showMemberEditForm() {
        return "/members/edit";
    }

    @GetMapping("/delete-page")
    public String showDeleteMemberPage() {
        return "/members/delete";
    }
}
