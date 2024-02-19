package dev.linkcentral.presentation.controller.view;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.infrastructure.jwt.JwtAuthenticationFilter;
import dev.linkcentral.infrastructure.jwt.JwtTokenProvider;
import dev.linkcentral.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

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
    public String memberEdit(Model model) {
        Member member = memberService.getCurrentMember();
        model.addAttribute("member", member);
        return "/members/edit";
    }

    @GetMapping("/edit-form2")
    public String memberEdit2() {
        return "/members/edit";
    }

    @GetMapping("/delete-page")
    public String logout() {
        return "/members/delete";
    }
}