package dev.linkcentral.presentation.controller.api.open;

import dev.linkcentral.infrastructure.jwt.JwtTokenDTO;
import dev.linkcentral.presentation.BaseUrlUtil;
import dev.linkcentral.presentation.request.member.MemberLoginRequest;
import dev.linkcentral.presentation.request.member.MemberSaveRequest;
import dev.linkcentral.presentation.response.member.LoginSuccessResponse;
import dev.linkcentral.presentation.response.member.MailPasswordResetResponse;
import dev.linkcentral.presentation.response.member.MemberPasswordResponse;
import dev.linkcentral.presentation.response.member.MemberSavedResponse;
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

    /**
     * 새로운 회원을 등록합니다.
     *
     * @param memberSaveRequest 회원 등록 요청
     * @return 회원 저장 응답
     */
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<MemberSavedResponse> register(@Validated @RequestBody MemberSaveRequest memberSaveRequest) {
        MemberRegistrationDTO memberDTO = MemberSaveRequest.toMemberRegistrationCommand(memberSaveRequest);
        MemberRegistrationResultDTO registrationResultDTO = memberFacade.registerNewMember(memberDTO);

        MemberSavedResponse response = MemberSavedResponse.toMemberSaveResponse(registrationResultDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 회원 로그인을 처리합니다.
     *
     * @param memberLoginRequest 회원 로그인 요청
     * @param request            HttpServletRequest 객체
     * @return 로그인 성공 응답
     */
    @PostMapping("/login")
    public ResponseEntity<LoginSuccessResponse> login(@Validated @RequestBody MemberLoginRequest memberLoginRequest,
                                                      HttpServletRequest request) {

        MemberLoginRequestDTO loginRequestDTO = MemberLoginRequest.toMemberLoginRequestCommand(memberLoginRequest);
        JwtTokenDTO jwtTokenDTO = memberFacade.loginMember(loginRequestDTO);

        final String redirectUrl = BaseUrlUtil.getBaseUrl(request) + "/api/v1/view/member/login";
        LoginSuccessResponse response = LoginSuccessResponse.toLoginSuccessResponse(jwtTokenDTO, redirectUrl);
        return ResponseEntity.ok(response);
    }

    /**
     * 비밀번호 유효성을 검사합니다.
     *
     * @param userEmail 사용자 이메일
     * @return 비밀번호 응답
     */
    @GetMapping("/forgot-password")
    @ResponseBody
    public ResponseEntity<MemberPasswordResponse> validatePassword(String userEmail) {
        boolean pwFindCheck = memberFacade.isPasswordValid(userEmail);
        MemberPasswordResponse response = MemberPasswordResponse.toMemberPasswordResponse(pwFindCheck);
        return ResponseEntity.ok(response);
    }

    /**
     * 비밀번호 재설정 이메일을 보냅니다.
     *
     * @param userEmail 사용자 이메일
     * @return 비밀번호 재설정 이메일 응답
     */
    @PostMapping("/send-email/update-password")
    public ResponseEntity<MailPasswordResetResponse> sendPasswordResetEmail(String userEmail) {
        memberFacade.sendPasswordResetEmail(userEmail);
        MailPasswordResetResponse response = MailPasswordResetResponse.toMailPasswordResetResponse();
        return ResponseEntity.ok(response);
    }

    /**
     * 현재 비밀번호를 확인합니다.
     *
     * @param password 현재 비밀번호
     * @return 비밀번호 응답
     */
    @PostMapping("/check-current-password")
    @ResponseBody
    public ResponseEntity<MemberPasswordResponse> verifyCurrentPassword(@RequestParam String password) {
        boolean isPasswordValid = memberFacade.checkPassword(password);
        MemberPasswordResponse response = MemberPasswordResponse.toMemberPasswordResponse(isPasswordValid);
        return ResponseEntity.ok(response);
    }
}
