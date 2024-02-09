package dev.linkcentral.presentation.controller.open;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import dev.linkcentral.common.exception.DuplicateNicknameException;
import dev.linkcentral.common.exception.MemberRegistrationException;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.infrastructure.jwt.JwtTokenDTO;
import dev.linkcentral.presentation.BaseUrlUtil;
import dev.linkcentral.presentation.response.LoginSuccessResponse;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.dto.request.MemberLoginRequestDTO;
import dev.linkcentral.service.dto.request.MemberMailRequestDTO;
import dev.linkcentral.service.dto.request.MemberSaveRequestDTO;
import dev.linkcentral.service.dto.response.MemberPasswordResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/api/v1/public/member")
public class MemberPublicController {
    private final MemberService memberService;

    @ResponseBody
    @PostMapping("/register")
    public ResponseEntity<?> register(@ModelAttribute MemberSaveRequestDTO memberDTO) {
        try {
            Member member = memberService.registerMember(memberDTO);
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
    public ResponseEntity<LoginSuccessResponse> login(@RequestBody MemberLoginRequestDTO memberLoginDTO,
                                                      HttpServletRequest request) {
        try {
            JwtTokenDTO jwtToken = memberService.authenticateAndGenerateJwtToken(memberLoginDTO.getEmail(),
                                                                                 memberLoginDTO.getPassword());
            final String redirectUrl = BaseUrlUtil.getBaseUrl(request) + "/api/v1/view/member/login";
            return ResponseEntity.ok(LoginSuccessResponse.builder()
                                                         .grantType(jwtToken.getGrantType())
                                                         .accessToken(jwtToken.getAccessToken())
                                                         .refreshToken(jwtToken.getRefreshToken())
                                                         .redirectUrl(redirectUrl)
                                                         .build()); // 정상적인 경우 JwtTokenDTO 반환
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 본문 없이 상태 코드만 반환
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 본문 없이 상태 코드만 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 본문 없이 상태 코드만 반환
        }
    }

//    @ResponseBody
//    @GetMapping("/login-success")
//    public String loginSuccess(Model model, HttpServletRequest request) {
//        String baseUrl = String.format("%s://%s:%d", request.getScheme(), request.getServerName(),
//                                       request.getServerPort());
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        log.info("Authentication 객체: {}", authentication);
//
//        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
//            log.info("인증 성공.");
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            model.addAttribute("memberName", userDetails.getUsername());
//            return baseUrl + "/api/v1/view/member/login"; // Redirect를 사용할 수 없는 이유는 ajax에서 redirect를 지원하지 않음
//        } else {
//            return baseUrl + "/";
//        }
//    }

    @GetMapping("/{nickname}/exists")
    public ResponseEntity<Boolean> isNicknameDuplicated(@PathVariable String nickname) {
        return ResponseEntity.ok(memberService.validateNicknameDuplication(nickname));
    }

    @ResponseBody
    @GetMapping("/forgot-password")
    public MemberPasswordResponseDTO isPasswordValid(String userEmail, String userName) {
        // 이메일과 이름이 일치하는 사용자가 있는지 확인.
        boolean pwFindCheck = memberService.validateUserEmail(userEmail, userName);
        return new MemberPasswordResponseDTO(pwFindCheck);
    }

    /**
     * 등록된 이메일로 임시비밀번호를 발송하고, 발송된 임시비밀번호로 사용자의 pw를 변경하는 API
     */
    @PostMapping("/send-email/update-password")
    public void sendEmail(String userEmail, String userName) {
        MemberMailRequestDTO dto = memberService.createMailForPasswordReset(userEmail, userName);
        memberService.sendMail(dto);
    }
}
