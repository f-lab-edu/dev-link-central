package dev.common.exception;

import dev.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    // ServiceException
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleServiceException(ServiceException ex) {
        CommonError commonError = ex.getCommonError();
        log.error("ServiceException 발생했습니다: {}", commonError.getMessage());
        return new ResponseEntity<>(
                ApiResponse.createApiResponseFromCommonError(commonError),
                commonError.getHttpStatus()
        );
    }

    // 기타 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        log.error("Unhandled Exception", ex);
        return new ResponseEntity<>(
                ApiResponse.createApiResponseFromCommonError(ErrorCode.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
