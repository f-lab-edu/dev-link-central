package dev.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements CommonError {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "400", "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "내부 서버 오류가 발생했습니다. 관리자에게 문의해주세요."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "405", "지원하지 않는 Http Method 입니다."),
    INVALID_PARAMETER_TYPE(HttpStatus.BAD_REQUEST, "400", "잘못된 파라미터 타입입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
