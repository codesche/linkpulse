package com.linkpulse.auth.controller;

import com.linkpulse.api.ApiResponse;
import com.linkpulse.auth.dto.request.LoginRequest;
import com.linkpulse.auth.dto.request.SignUpRequest;
import com.linkpulse.auth.dto.response.SignUpResponse;
import com.linkpulse.auth.dto.response.TokenResponse;
import com.linkpulse.auth.service.AuthService;
import com.linkpulse.trace.TraceIdFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ApiResponse<SignUpResponse> signUp(@RequestBody @Valid SignUpRequest req) {
        String id = authService.signUp(req);
        String traceId = MDC.get(TraceIdFilter.TRACE_ID);
        return ApiResponse.ok(SignUpResponse.builder().memberId(id).build(), traceId);
    }

    // 로그인 - 액세스/리프레시 토큰 발급
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@RequestBody @Valid LoginRequest req) {
        TokenResponse token = authService.login(req);
        String traceId = MDC.get(TraceIdFilter.TRACE_ID);
        return ApiResponse.ok(token, traceId);
    }

    // 토큰 리프레시
    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@RequestParam("memberId") String memberId,
                                            @RequestParam("username") String username,
                                            @RequestParam("refreshToken") String refreshToken) {
        TokenResponse token = authService.refresh(memberId, username, refreshToken);
        String traceId = MDC.get(TraceIdFilter.TRACE_ID);
        return ApiResponse.ok(token, traceId);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestParam("memberId") String memberId) {
        authService.logout(memberId);
        String traceId = MDC.get(TraceIdFilter.TRACE_ID);
        return ApiResponse.ok(null, traceId);
    }

}
