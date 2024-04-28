package dev.linkcentral.presentation.controller.api.open;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.infrastructure.jwt.JwtTokenDTO;
import dev.linkcentral.presentation.BaseUrlUtil;
import dev.linkcentral.service.dto.MemberMailDTO;
import dev.linkcentral.service.dto.MemberRegistrationDTO;
import dev.linkcentral.presentation.request.member.MemberLoginRequest;
import dev.linkcentral.presentation.request.member.MemberSaveRequest;
import dev.linkcentral.presentation.response.member.MailPasswordResetResponse;
import dev.linkcentral.presentation.response.member.MemberPasswordResponse;
import dev.linkcentral.presentation.response.member.MemberSaveResponse;
import dev.linkcentral.presentation.response.member.RegistrationErrorResponse;
import dev.linkcentral.presentation.response.member.LoginSuccessResponse;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/member")
public class MemberPublicController {

    private final MemberService memberService;
    private final MemberMapper memberMapper;

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> register(@Valid @RequestBody MemberSaveRequest memberSaveRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new RegistrationErrorResponse(errorMessage));
        }

        MemberRegistrationDTO memberDTO = memberMapper.toMemberRegistrationDTO(memberSaveRequest);
        Member member = memberService.registerMember(memberDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MemberSaveResponse(member.getId(), "회원 등록 성공"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginSuccessResponse> login(@Valid @RequestBody MemberLoginRequest memberLoginRequest,
                                                      HttpServletRequest request) {
        JwtTokenDTO jwtToken = memberService.authenticateAndGenerateJwtToken(
                memberLoginRequest.getEmail(), memberLoginRequest.getPassword());

        final String redirectUrl = BaseUrlUtil.getBaseUrl(request) + "/api/v1/view/member/login";
        LoginSuccessResponse response = memberMapper.toLoginSuccessResponse(jwtToken, redirectUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/forgot-password")
    @ResponseBody
    public ResponseEntity<MemberPasswordResponse> isPasswordValid(String userEmail, String userName) {
        boolean pwFindCheck = memberService.validateUserEmail(userEmail, userName);
        return ResponseEntity.ok(new MemberPasswordResponse(pwFindCheck));
    }

    @PostMapping("/send-email/update-password")
    public ResponseEntity<MailPasswordResetResponse> sendEmail(String userEmail, String userName) {
        MemberMailDTO memberMailDTO = memberService.createMailForPasswordReset(userEmail, userName);
        memberService.sendMail(memberMailDTO);
        return ResponseEntity.ok(new MailPasswordResetResponse(true, "임시 비밀번호가 이메일로 발송되었습니다."));
    }

    @PostMapping("/check-current-password")
    @ResponseBody
    public ResponseEntity<MemberPasswordResponse> checkPassword(@RequestParam String password) {
        Member member = memberService.getCurrentMember();
        boolean isPasswordValid = memberService.validatePassword(member.getNickname(),password);
        return ResponseEntity.ok(new MemberPasswordResponse(isPasswordValid));
    }

}
