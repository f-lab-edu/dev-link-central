package dev.linkcentral.presentation.controller.api.open;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.infrastructure.jwt.JwtTokenDTO;
import dev.linkcentral.presentation.BaseUrlUtil;
import dev.linkcentral.presentation.dto.MemberRegistrationDTO;
import dev.linkcentral.presentation.dto.request.MemberLoginRequest;
import dev.linkcentral.presentation.dto.request.MemberMailRequest;
import dev.linkcentral.presentation.dto.request.MemberSaveRequest;
import dev.linkcentral.presentation.dto.response.MemberPasswordResponse;
import dev.linkcentral.presentation.dto.response.MemberSaveResponse;
import dev.linkcentral.presentation.dto.response.RegistrationErrorResponse;
import dev.linkcentral.presentation.response.LoginSuccessResponse;
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
    public MemberPasswordResponse isPasswordValid(String userEmail, String userName) {
        // 이메일과 이름이 일치하는 사용자가 있는지 확인.
        boolean pwFindCheck = memberService.validateUserEmail(userEmail, userName);
        return new MemberPasswordResponse(pwFindCheck);
    }

    /**
     * 등록된 이메일로 임시비밀번호를 발송하고, 발송된 임시비밀번호로 사용자의 pw를 변경하는 API
     */
    @PostMapping("/send-email/update-password")
    public void sendEmail(String userEmail, String userName) {
        MemberMailRequest dto = memberService.createMailForPasswordReset(userEmail, userName);
        memberService.sendMail(dto);
    }

    @PostMapping("/check-current-password")
    @ResponseBody
    public MemberPasswordResponse checkPassword(@RequestParam String password) {
        Member member = memberService.getCurrentMember();
        boolean result = memberService.validatePassword(member.getNickname(), password);
        return new MemberPasswordResponse(result);
    }
}