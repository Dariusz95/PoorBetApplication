package com.poorbet.users.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final LocalDateTime timestamp;
    private final int status;
    private final String message;
    private final String errorCode;
    private final String path;
    private final T data;
    private final List<ValidationError> validationErrors;

    @Builder
    @Getter
    public static class ValidationError {
        private final String field;
        private final String message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Success")
                .data(data)
                .build();
    }

    public static ApiResponse<Void> error(int status, String message, String errorCode, String path) {
        return ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .message(message)
                .errorCode(errorCode)
                .path(path)
                .build();
    }
}