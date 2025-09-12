package com.linkpulse.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "LP-VALIDATION", "Validation error"),
    AUTH_ERROR(HttpStatus.UNAUTHORIZED, "LP-AUTH", "Authentication error"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "LP-FORBIDDEN", "Forbidden"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "LP-NOT-FOUND", "Resource not found"),
    CONFLICT(HttpStatus.CONFLICT, "LP-CONFLICT", "Conflict"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "LP-ERR", "Internal server error");

    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;
}
