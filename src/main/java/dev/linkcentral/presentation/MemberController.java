package dev.linkcentral.presentation;

import dev.linkcentral.common.exception.DuplicateNicknameException;
import dev.linkcentral.common.exception.MemberRegistrationException;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.dto.request.MemberLoginRequestDTO;
import dev.linkcentral.service.dto.request.MemberMailRequestDTO;
import dev.linkcentral.service.dto.request.MemberSaveRequestDTO;
import dev.linkcentral.service.dto.request.MemberEditRequestDTO;
import dev.linkcentral.service.dto.response.MemberEditResponseDTO;
import dev.linkcentral.service.dto.response.MemberPasswordResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
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

    @ResponseBody
    @PostMapping("/new")
    public ResponseEntity<String> createMember(@ModelAttribute MemberSaveRequestDTO memberDTO) {
        try {
            Member member = memberService.joinMember(memberDTO);
            return ResponseEntity.status(HttpStatus.FOUND).header("Location", "/").build();
        } catch (DuplicateNicknameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("닉네임이 이미 사용 중입니다.");
        } catch (MemberRegistrationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 가입 중 오류가 발생했습니다.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public String login(@ModelAttribute MemberLoginRequestDTO MemberLoginDTO, HttpSession session, Model model) {
        Optional<Member> member = memberService.loginMember(MemberLoginDTO.getEmail(), MemberLoginDTO.getPassword());

        if (member.isEmpty()) {
            model.addAttribute("loginMessage", "아이디 혹은 비밀번호가 일치하지 않습니다.");
            return "/home";
        }
        session.setAttribute("member", member.get());
        member.map(Member::getName).ifPresent(memberName -> model.addAttribute("memberName", memberName));
        return "/members/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return "redirect:/";
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
    public MemberPasswordResponseDTO isPasswordValid(String userEmail, String userName) {
        // 이메일과 이름이 일치하는 사용자가 있는지 확인.
        boolean pwFindCheck = memberService.userEmailCheck(userEmail, userName);
        return new MemberPasswordResponseDTO(pwFindCheck);
    }

    /**
     * 등록된 이메일로 임시비밀번호를 발송하고, 발송된 임시비밀번호로 사용자의 pw를 변경하는 API
     */
    @PostMapping("/send-email/update-password")
    public void sendEmail(String userEmail, String userName) {
        MemberMailRequestDTO dto = memberService.createMailAndChangePassword(userEmail, userName);
        memberService.mailSend(dto);
    }

    @GetMapping("/edit-form")
    public String memberEdit(HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("member");

        if (member == null) {
            return "redirect:/login";
        }
        model.addAttribute("member", member);
        return "/members/edit";
    }

    @ResponseBody
    @PostMapping("/check-current-password")
    public MemberPasswordResponseDTO checkPassword(@RequestParam String password, HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        boolean result = memberService.checkPassword(member.getNickname(), password);
        return new MemberPasswordResponseDTO(result);
    }

    @ResponseBody
    @PutMapping("/edit")
    public MemberEditResponseDTO memberUpdate(@ModelAttribute MemberEditRequestDTO memberEditDTO) {
        memberService.updateMember(memberEditDTO);
        return new MemberEditResponseDTO(HttpStatus.OK.value());
    }

    @GetMapping("/api/delete-page")
    public String logout() {
        return "/members/delete";
    }

    @PostMapping("/delete")
    public String checkPassword(@RequestParam String password, HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("member");
        boolean result = memberService.deleteMember(member.getNickname(), password);

        if (result) {
            return "redirect:/";
        }
        model.addAttribute("message", "비밀번호가 일치하지 않습니다.");
        return "members/delete";
    }
}