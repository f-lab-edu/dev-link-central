package dev.linkcentral.common.exception.handler;

import dev.linkcentral.common.exception.DuplicateEmailException;
import dev.linkcentral.common.exception.DuplicateNicknameException;
import dev.linkcentral.common.exception.MemberRegistrationException;
import dev.linkcentral.presentation.dto.response.AuthenticationErrorResponse;
import dev.linkcentral.presentation.dto.response.RegistrationErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<AuthenticationErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AuthenticationErrorResponse("사용자를 찾을 수 없습니다."));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<RegistrationErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("삭제 중 오류 발생: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new RegistrationErrorResponse("해당 항목을 찾을 수 없어 삭제를 실패했습니다.", ex.getLocalizedMessage()));
    }
}
