package dev.common;

import dev.linkcentral.presentation.response.profile.ProfileUpdatedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {



    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ProfileUpdatedResponse> handleProfileUsernameNotFoundException(UsernameNotFoundException ex) {
        ProfileUpdatedResponse response = ProfileUpdatedResponse.builder()
                .message("인증되지 않은 사용자입니다.")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<Object> handleProfileNotFoundException(ProfileNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ProfileUpdatedResponse.builder().message(ex.getMessage()).build()
        );
    }

    @ExceptionHandler(ProfileUpdateException.class)
    public ResponseEntity<Object> handleProfileUpdateException(ProfileUpdateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ProfileUpdatedResponse.builder().message("프로필 업데이트에 실패했습니다.").build()
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException e, Model model) {
        model.addAttribute("error", "해당 memberId에 대한 프로필을 찾을 수 없습니다.");
        return "error";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<String> handleMemberNotFound(MemberNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
