package dev.linkcentral.presentation;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.dto.MemberMailRequestDTO;
import dev.linkcentral.service.dto.MemberLoginRequestDTO;
import dev.linkcentral.service.dto.MemberSaveRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MemberController {

    private final MemberService memberService;

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

    @GetMapping("/members/{nickname}/exists")
    public ResponseEntity<Boolean> isNicknameDuplicated(@PathVariable String nickname) {
        return ResponseEntity.ok(memberService.isNicknameDuplicated(nickname));
    }

    @GetMapping("reset-password")
    public String mailAndChangePassword() {
        return "/members/reset-password";
    }

    @ResponseBody
    @GetMapping("/forgot-password")
    public Map<String, Boolean> isPasswordValid(String userEmail, String userName) {
        Map<String, Boolean> json = new HashMap<>();

        // 이메일과 이름이 일치하는 사용자가 있는지 확인.
        boolean pwFindCheck = memberService.userEmailCheck(userEmail, userName);

        json.put("check", pwFindCheck);
        return json;
    }


    // 등록된 이메일로 임시비밀번호를 발송하고, 발송된 임시비밀번호로 사용자의 pw를 변경하는 API
    @PostMapping("/send-email/update-password")
    public void sendEmail(String userEmail, String userName) {
        MemberMailRequestDTO dto = memberService.createMailAndChangePassword(userEmail, userName);
        memberService.mailSend(dto);
    }
}