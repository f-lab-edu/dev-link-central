package dev.member.controller.api.open;

import dev.common.dto.ApiResponse;
import dev.common.jwt.JwtToken;
import dev.member.controller.request.SignInRequest;
import dev.member.controller.request.SignUpRequest;
import dev.member.service.MemberAuthService;
import dev.member.service.MemberService;
import dev.member.service.dto.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/members")
public class MemberPublicController {

    private final MemberService memberService;
    private final MemberAuthService memberAuthService;

    @PostMapping
    public ResponseEntity<ApiResponse<SignUpResponse>> signup(
            @Validated @RequestBody SignUpRequest signUpRequest
    ) {
        SignUpResponse signup = memberService.signup(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.createApiResponse(signup));
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<JwtToken>> signIn(
            @Validated @RequestBody SignInRequest signInRequest
    ) {
        JwtToken jwtToken = memberAuthService.signIn(signInRequest);
        return ResponseEntity.ok(ApiResponse.createApiResponse(jwtToken));
    }

    @PostMapping("/check-password")
    public ResponseEntity<ApiResponse<Boolean>> checkPassword(
            @RequestParam String password
    ) {
        boolean exists = memberAuthService.checkPassword(password);
        return ResponseEntity.ok(ApiResponse.createApiResponse(exists));
    }

    @GetMapping("/password/check-email")
    public ResponseEntity<ApiResponse<Boolean>> existsByEmail(
            @RequestParam String email
    ) {
        boolean exists = memberService.existsByEmail(email);
        return ResponseEntity.ok(ApiResponse.createApiResponse(exists));
    }

    @PostMapping("/password/reset-email")
    public ResponseEntity<ApiResponse<Void>> sendPasswordResetMail(
            @RequestParam String email
    ) {
        memberService.sendPasswordResetMail(email);
        return ResponseEntity.ok(ApiResponse.createEmptyApiResponse());
    }
}
