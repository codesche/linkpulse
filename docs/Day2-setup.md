
# Day2 — 공통 계층 (응답/예외/트레이싱) 정리

## 🎯 목표
- 모든 서비스에서 **통일된 응답 포맷** 사용
- **전역 예외 처리** 적용
- 요청 단위로 **traceId** 생성/로깅/응답 포함
- 규칙 반영:
    - 글로벌 예외 처리 필요
    - 공통 API 응답 객체 필요
    - LocalDateTime 대신 Instant 사용 준비
    - @Setter 지양, @Builder 권장

---

## 📦 common-lib 모듈

### 1. `ApiResponse<T>`
- 성공/실패 응답 공통 포맷
- traceId 포함

```java
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
```

### 2. `ErrorCode`

```java
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
```

<br/>

### 3. `BaseException`

```java
@Getter
public class BaseException extends RuntimeException {
    private final ErrorCode errorCode;

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public BaseException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}

```

<br/>

### 4. `TraceIdFilter`

- 요청 단위 traceId 생성 및 응답 헤더(X-Trace-Id) 삽입
- MDC에 traceId 저장 → 로깅/ApiResponse에서 활용

```java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter implements Filter {
    public static final String TRACE_ID = "traceId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String traceId = UUID.randomUUID().toString();
        try {
            MDC.put(TRACE_ID, traceId);
            if (response instanceof HttpServletResponse httpResponse) {
                httpResponse.setHeader("X-Trace-Id", traceId);
            }
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID);
        }
    }
}

```

<br/>

### 5. `GlobalExceptionHandler`

- 전역 예외 처리
- 모든 에러를 `ApiResponse.error` 포맷으로 변환

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    private String traceId() {
        return MDC.get("traceId");
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<?>> handleBase(BaseException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.getCode(), ex.getMessage(), traceId()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(objectError -> objectError.getDefaultMessage())
                .orElse(ErrorCode.VALIDATION_ERROR.getDefaultMessage());
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.getCode(), message, traceId()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraint(ConstraintViolationException ex) {
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.getCode(), ex.getMessage(), traceId()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAny(Exception ex) {
        ErrorCode errorCode = ErrorCode.SERVER_ERROR;
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.getCode(), errorCode.getDefaultMessage(), traceId()));
    }
}
```

### 6. 각 서비스 적용

```java
@RestController
public class HealthController {

    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.ok("auth-ok", MDC.get(TraceIdFilter.TRACE_ID));  // 서비스별로 메시지 변경
    }

}
```