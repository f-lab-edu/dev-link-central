package dev.linkcentral.presentation.controller.api.open;

import dev.linkcentral.presentation.BaseUrlUtil;
import dev.linkcentral.presentation.request.member.MemberLoginRequest;
import dev.linkcentral.presentation.request.member.MemberSaveRequest;
import dev.linkcentral.presentation.response.member.LoginSuccessResponse;
import dev.linkcentral.presentation.response.member.MailPasswordResetResponse;
import dev.linkcentral.presentation.response.member.MemberPasswordResponse;
import dev.linkcentral.presentation.response.member.MemberSaveResponse;
import dev.linkcentral.infrastructure.jwt.JwtTokenDTO;
import dev.linkcentral.service.dto.member.MemberLoginRequestDTO;
import dev.linkcentral.service.dto.member.MemberRegistrationDTO;
import dev.linkcentral.service.dto.member.MemberRegistrationResultDTO;
import dev.linkcentral.service.facade.MemberFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/member")
public class MemberPublicController {

    private final MemberFacade memberFacade;

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<MemberSaveResponse> register(@Validated @RequestBody MemberSaveRequest memberSaveRequest) {
        MemberRegistrationDTO memberDTO = MemberSaveRequest.toMemberRegistrationCommand(memberSaveRequest);
        MemberRegistrationResultDTO registrationResultDTO = memberFacade.registerNewMember(memberDTO);

        MemberSaveResponse response = MemberSaveResponse.toMemberSaveResponse(registrationResultDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginSuccessResponse> login(@Validated @RequestBody MemberLoginRequest memberLoginRequest,
                                                      HttpServletRequest request) {
        MemberLoginRequestDTO loginRequestDTO = MemberLoginRequest.toMemberLoginRequestCommand(memberLoginRequest);
        JwtTokenDTO jwtTokenDTO = memberFacade.loginMember(loginRequestDTO);

        final String redirectUrl = BaseUrlUtil.getBaseUrl(request) + "/api/v1/view/member/login";
        LoginSuccessResponse response = LoginSuccessResponse.toLoginSuccessResponse(jwtTokenDTO, redirectUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/forgot-password")
    @ResponseBody
    public ResponseEntity<MemberPasswordResponse> validatePassword(String userEmail, String userName) {
        boolean pwFindCheck = memberFacade.isPasswordValid(userEmail, userName);
        MemberPasswordResponse response = MemberPasswordResponse.toMemberPasswordResponse(pwFindCheck);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-email/update-password")
    public ResponseEntity<MailPasswordResetResponse> sendPasswordResetEmail(String userEmail, String userName) {
        memberFacade.sendPasswordResetEmail(userEmail, userName);
        MailPasswordResetResponse response = MailPasswordResetResponse.toMailPasswordResetResponse();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/check-current-password")
    @ResponseBody
    public ResponseEntity<MemberPasswordResponse> verifyCurrentPassword(@RequestParam String password) {
        boolean isPasswordValid = memberFacade.checkPassword(password);
        MemberPasswordResponse response = MemberPasswordResponse.toMemberPasswordResponse(isPasswordValid);
        return ResponseEntity.ok(response);
    }

}
