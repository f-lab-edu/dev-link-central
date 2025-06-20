package dev.common.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import dev.common.exception.CommonError;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {

    private static final String SUCCESS_CODE = "20000";
    private static final String SUCCESS_MESSAGE = "success";

    private final String code;
    private final String message;

    @JsonUnwrapped
    private final T body;

    // 데이터 없는 성공 응답 (수정/삭제 등)
    public static ApiResponse<Void> createEmptyApiResponse() {
        return ApiResponse.<Void>builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .body(null)
                .build();
    }

    // 데이터 있는 성공 응답 (상세조회, 목록 등)
    public static <T> ApiResponse<T> createApiResponse(T body) {
        return ApiResponse.<T>builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .body(body)
                .build();
    }

    public static ApiResponse<Void> createApiResponseFromCommonError(CommonError error) {
        return ApiResponse.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .body(null)
                .build();
    }
}
