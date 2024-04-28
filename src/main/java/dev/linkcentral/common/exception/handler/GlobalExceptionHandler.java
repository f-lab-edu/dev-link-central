package dev.linkcentral.common.exception.handler;

import dev.linkcentral.common.exception.*;
import dev.linkcentral.presentation.response.member.AuthenticationErrorResponse;
import dev.linkcentral.presentation.response.member.RegistrationErrorResponse;
import dev.linkcentral.presentation.response.profile.ProfileUpdateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RegistrationErrorResponse> handleException(Exception ex) {
        log.error("내부 서버 오류 발생: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RegistrationErrorResponse("내부 서버 오류입니다.", ex.getLocalizedMessage()));
    }

    @ExceptionHandler({DuplicateNicknameException.class, DuplicateEmailException.class})
    public ResponseEntity<RegistrationErrorResponse> handleDuplicateException(RuntimeException ex) {
        log.warn("등록 실패: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new RegistrationErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MemberRegistrationException.class)
    public ResponseEntity<RegistrationErrorResponse> handleMemberRegistrationException(MemberRegistrationException ex) {
        log.error("회원 가입 중 오류 발생: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RegistrationErrorResponse("회원 가입 중 오류가 발생했습니다.", ex.getLocalizedMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<AuthenticationErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthenticationErrorResponse("잘못된 인증 정보입니다."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RegistrationErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("요청 검증 실패: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new RegistrationErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ProfileUpdateResponse> handleProfileUsernameNotFoundException(UsernameNotFoundException ex) {
        ProfileUpdateResponse response = ProfileUpdateResponse.builder()
                .message("인증되지 않은 사용자입니다.")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<Object> handleProfileNotFoundException(ProfileNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ProfileUpdateResponse.builder().message(ex.getMessage()).build()
        );
    }

    @ExceptionHandler(ProfileUpdateException.class)
    public ResponseEntity<Object> handleProfileUpdateException(ProfileUpdateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ProfileUpdateResponse.builder().message("프로필 업데이트에 실패했습니다.").build()
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException e, Model model) {
        model.addAttribute("error", "해당 memberId에 대한 프로필을 찾을 수 없습니다.");
        return "error";
    }

}
