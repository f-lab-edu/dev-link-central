package dev.linkcentral.presentation;

import dev.linkcentral.database.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/view/member")
public class MemberViewController {

    @GetMapping("/")
    public String Home() {
        return "/home";
    }

    @GetMapping("/join-form")
    public String joinMember() {
        return "/members/join";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return "redirect:/";
    }

    @GetMapping("reset-password")
    public String mailAndChangePassword() {
        return "/members/reset-password";
    }

    @GetMapping("/edit-form")
    public String memberEdit(HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("member");

        if (member == null) {
            return "redirect:/api/v1/member/login";
        }
        model.addAttribute("member", member);
        return "/members/edit";
    }

    @GetMapping("/delete-page")
    public String logout() {
        return "/members/delete";
    }
}