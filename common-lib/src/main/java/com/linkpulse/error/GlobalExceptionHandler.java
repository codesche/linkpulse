package com.linkpulse.error;

import com.linkpulse.api.ApiResponse;
import com.linkpulse.trace.TraceIdFilter;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private String traceId() {
        return MDC.get(TraceIdFilter.TRACE_ID);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<?>> handleBase(BaseException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ApiResponse<?> body = ApiResponse.error(errorCode.getCode(), ex.getMessage(), traceId());
        return ResponseEntity.status(errorCode.getStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().stream()
            .findFirst()
            .map(objectError -> objectError.getDefaultMessage())
            .orElse(ErrorCode.VALIDATION_ERROR.getDefaultMessage());

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        ApiResponse<?> body = ApiResponse.error(errorCode.getCode(), message, traceId());
        return ResponseEntity.status(errorCode.getStatus()).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraint(ConstraintViolationException ex) {
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        ApiResponse<?> body = ApiResponse.error(errorCode.getCode(), ex.getMessage(), traceId());
        return ResponseEntity.status(errorCode.getStatus()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAny(Exception ex) {
        ErrorCode errorCode = ErrorCode.SERVER_ERROR;
        ApiResponse<?> body = ApiResponse.error(errorCode.getCode(), errorCode.getDefaultMessage(), traceId());
        return ResponseEntity.status(errorCode.getStatus()).body(body);
    }

}
