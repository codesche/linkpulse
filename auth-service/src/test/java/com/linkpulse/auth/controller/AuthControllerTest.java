package com.linkpulse.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkpulse.auth.dto.request.LoginRequest;
import com.linkpulse.auth.dto.request.SignUpRequest;
import com.linkpulse.auth.dto.response.SignUpResponse;
import com.linkpulse.auth.dto.response.TokenResponse;
import com.linkpulse.auth.service.AuthService;
import com.linkpulse.config.TestSecurityConfig;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @MockitoBean
    private AuthService authService;

    @DisplayName("로그인 성공: 요청/응답 포맷 + permitAll")
    @Test
    public void login_success() throws Exception {
        Mockito.when(authService.login(any(LoginRequest.class)))
            .thenReturn(TokenResponse.builder()
                .accessToken("jwt-access")
                .refreshToken("jwt-refresh")
                .expiresInSeconds(3600L)
                .build());

        LoginRequest req = LoginRequest.builder()
            .username("alice")
            .password("Ab123456!")
            .build();

        mvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code", is("LP-OK")))
            .andExpect(jsonPath("$.data.accessToken", not(emptyString())))
            .andExpect(jsonPath("$.data.refreshToken", not(emptyString())))
            .andExpect(jsonPath("$.data.expiresInSeconds", greaterThan(0)));
    }

    @Test
    @DisplayName("로그인 검증 실패: LP-VALIDATION 코드 매핑")
    public void login_validation_fail() throws Exception {
        // username 누락 -> @Valid 검증 실패 -> GlobalExceptionHandler -> LP-VALIDATION
        LoginRequest bad = LoginRequest.builder()
            .password("Ab123456!")
            .build();

        mvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(bad)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code", is("LP-VALIDATION")))
            .andExpect(jsonPath("$.message", not(emptyString())))
            .andExpect(jsonPath("$.errors", notNullValue()));
    }

    @Test
    @DisplayName("회원가입 성공: 응답 포맷 + permitAll")
    public void signup_success() throws Exception {
        Mockito.when(authService.signUp(any(SignUpRequest.class)))
            .thenReturn(String.valueOf(SignUpResponse.builder()
                .memberId("1b8ea8c8-1111-4111-8111-1a1a1a1a1a1a")
                .build()));

        SignUpRequest req = SignUpRequest.builder()
            .username("codeshce")
            .password("Pw123456!")
            .nickName("jonathan")
            .build();

        mvc.perform(post("/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is("LP-OK")))
            .andExpect(jsonPath("$.data.memberId", not(emptyString())));
    }


}