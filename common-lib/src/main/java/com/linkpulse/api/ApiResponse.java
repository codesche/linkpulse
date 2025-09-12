package com.linkpulse.api;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T> {
    private final boolean success;
    private final String code;
    private final String message;
    private final String traceId;
    private final T data;

    public static <T> ApiResponse<T> ok(T data, String traceId) {
        return ApiResponse.<T>builder()
            .success(true)
            .code("LP-OK")
            .message("OK")
            .traceId(traceId)
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> error(String code, String message, String traceId) {
        return ApiResponse.<T>builder()
            .success(false)
            .code(code)
            .message(message)
            .traceId(traceId)
            .data(null)
            .build();
    }

}
