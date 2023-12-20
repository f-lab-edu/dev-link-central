package dev.linkcentral.presentation;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.dto.MemberLoginRequestDTO;
import dev.linkcentral.service.dto.MemberSaveRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MemberController {

    private final MemberService memberService;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String Home() {
        return "/home";
    }

    @GetMapping("/members/join-form")
    public String joinMember() {
        return "/members/join";
    }

    @PostMapping("/new")
    public String createMember(@ModelAttribute MemberSaveRequestDTO memberSaveRequestDTO) {
        memberSaveRequestDTO.encryptPassword(passwordEncoder.encode(memberSaveRequestDTO.getPassword()));
        Long memberId = memberService.joinMember(memberSaveRequestDTO);

        return "/home";
    }

    @PostMapping("/login")
    public String login(MemberLoginRequestDTO MemberLoginRequestDTO, Model model) {
        Optional<Member> member = memberService.loginMember(MemberLoginRequestDTO.getName(), MemberLoginRequestDTO.getPassword());

        if (member.isEmpty()) {
            model.addAttribute("loginMessage", "아이디 혹은 비밀번호가 일치하지 않습니다.");
            return "/home";
        }
        member.map(Member::getName).ifPresent(memberName -> model.addAttribute("memberName", memberName));
        return "/members/login";
    }

}