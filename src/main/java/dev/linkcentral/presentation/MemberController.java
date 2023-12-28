package dev.linkcentral.presentation;

import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.dto.MemberSaveRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
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
}