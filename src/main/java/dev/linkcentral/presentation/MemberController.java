package dev.linkcentral.presentation;

import dev.linkcentral.common.exception.DuplicateNicknameException;
import dev.linkcentral.common.exception.MemberRegistrationException;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.infrastructure.jwt.JwtTokenDTO;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.dto.request.MemberEditRequestDTO;
import dev.linkcentral.service.dto.request.MemberLoginRequestDTO;
import dev.linkcentral.service.dto.request.MemberMailRequestDTO;
import dev.linkcentral.service.dto.request.MemberSaveRequestDTO;
import dev.linkcentral.service.dto.response.MemberEditResponseDTO;
import dev.linkcentral.service.dto.response.MemberPasswordResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/")
    public String Home() {
        return "/home";
    }

    @GetMapping("/join-form")
    public String joinMember() {
        return "/members/join";
    }

    @ResponseBody
    @PostMapping("/register")
    public ResponseEntity<?> register(@ModelAttribute MemberSaveRequestDTO memberDTO) {
        try {
            Member member = memberService.joinMember(memberDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DuplicateNicknameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("닉네임이 이미 사용 중입니다.");
        } catch (MemberRegistrationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 가입 중 오류가 발생했습니다.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberLoginRequestDTO memberLoginDTO) {
        try {
            JwtTokenDTO jwtToken = memberService.signIn(memberLoginDTO.getEmail(), memberLoginDTO.getPassword());
            return ResponseEntity.ok(jwtToken);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 혹은 비밀번호가 일치하지 않습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인 처리 중 오류 발생");
        }
    }

    @ResponseBody
    @GetMapping("/login-success")
    public String loginSuccess(Model model, HttpServletRequest request) {
        String baseUrl = String.format("%s://%s:%d",request.getScheme(),  request.getServerName(), request.getServerPort());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authentication 객체: {}", authentication);

        if (authentication != null && authentication.isAuthenticated()) {
            log.info("인증 성공.");
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("memberName", userDetails.getUsername());
            return baseUrl + "/members/login"; // Redirect를 사용할 수 없는 이유는 ajax에서 redirect를 지원하지 않음
        } else {
            return baseUrl + "/";
        }
    }

    @GetMapping("/{nickname}/exists")
    public ResponseEntity<Boolean> isNicknameDuplicated(@PathVariable String nickname) {
        return ResponseEntity.ok(memberService.isNicknameDuplicated(nickname));
    }

    @GetMapping("/reset-password")
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

    @GetMapping("/delete-page")
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