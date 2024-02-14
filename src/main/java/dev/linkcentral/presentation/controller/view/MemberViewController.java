package dev.linkcentral.presentation.controller.view;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

//    @GetMapping("/edit-form")
//    public String memberEdit(Model model) {
//        Member member = memberService.getCurrentMember();
//        if (member == null) {
//            return "redirect:/api/v1/view/member/login";
//        }
//        model.addAttribute("member", member);
//        return "/members/edit";
//    }

    /**
     * 일단 여기서 테스트.
     */
    @GetMapping("/edit-form")
    public ResponseEntity<?> memberEdit() {
        Member member = memberService.getCurrentMember();
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(Collections.singletonMap("url", "/members/edit"));
    }

    @GetMapping("/delete-page")
    public String logout() {
        return "/members/delete";
    }
}