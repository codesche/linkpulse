package com.linkpulse.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.linkpulse.auth.domain.Member;
import com.linkpulse.auth.dto.request.LoginRequest;
import com.linkpulse.auth.dto.request.SignUpRequest;
import com.linkpulse.auth.dto.response.TokenResponse;
import com.linkpulse.auth.token.JwtTokenProvider;
import com.linkpulse.auth.token.RedisRefreshToken;
import com.linkpulse.error.BaseException;
import com.linkpulse.error.ErrorCode;
import com.linkpulse.repository.MemberRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

class AuthServiceTest {

    private MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private RedisRefreshToken redisRefreshToken;
    private AuthService authService;

    @BeforeEach
    @DisplayName("각 테스트 실행 전: Mock 초기화 및 AuthService 주입")
    public void setUp() {
        memberRepository = Mockito.mock(MemberRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        jwtTokenProvider = Mockito.mock(JwtTokenProvider.class);
        redisRefreshToken = Mockito.mock(RedisRefreshToken.class);

        // 테스트 대상 서비스에 정확히 위에서 만든 mock들을 주입
        authService = new AuthService(memberRepository, passwordEncoder, jwtTokenProvider, redisRefreshToken);
    }

    @DisplayName("회원가입: username 중복이면 LP-CONFLICT 예외가 발생한다")
    @Test
    public void signUp_whenUsernameExists_thenThrowConflict() {
        when(memberRepository.existsByUsername("alice")).thenReturn(true);

        SignUpRequest req = SignUpRequest.builder()
            .username("alice")
            .password("pw123456!")
            .nickName("앨리스")
            .build();

        BaseException ex = assertThrows(BaseException.class, () -> authService.signUp(req));
        assertEquals(ErrorCode.CONFLICT, ex.getErrorCode());
        verify(memberRepository, times(1)).existsByUsername("alice");
        verify(memberRepository, never()).save(any());
    }

    @DisplayName("로그인: 자격 증명이 올바르면 access/refresh 토큰을 발급한다")
    @Test
    public void login_whenCredentialsOk_thenIssueTokens() {
        Member member = Member.builder()
            .username("alice")
            .passwordHash("ENC")
            .nickName("앨리스")
            .build();

        // 서비스는 member.getId()를 JWT subject로 사용한다.
        // 테스트에서는 영속화가 아니므로 id를 수동으로 세팅해줘야 any(UUID.class) 매칭이 동작한다.
        UUID memberId = UUID.randomUUID();
        ReflectionTestUtils.setField(member, "id", memberId);

        when(memberRepository.findByUsername("alice")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("Pw123456!", "ENC")).thenReturn(true);

        // id와 username이 정확히 전달되었는지 검증하기 위해 eq()로 매칭
        when(jwtTokenProvider.createAccessToken(any(UUID.class), eq("alice"))).thenReturn("jwt-access");

        LoginRequest req = LoginRequest.builder()
            .username("alice")
            .password("Pw123456!")
            .build();

        // when
        TokenResponse token = authService.login(req);

        // then
        assertEquals("jwt-access", token.getAccessToken());     // access 토큰이 기대값으로 설정되었는지 확인
        assertNotNull(token.getRefreshToken());                            // refresh 토큰이 발급되었는지
        assertTrue(token.getExpiresInSeconds() > 0L);           // 만료 시간이 양수인지
        verify(redisRefreshToken, times(1)).save(any(UUID.class), anyString());     // refresh 저장 호출 검증
    }

    @DisplayName("로그인: 비밀번호가 틀리면 LP-AUTH 예외가 발생한다")
    @Test
    public void login_whenWrongPassword_thenAuthError() {
        Member member = Member.builder()
            .username("alice")
            .passwordHash("ENC")
            .nickName("앨리스")
            .build();

        when(memberRepository.findByUsername("alice")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("wrong", "ENC")).thenReturn(false);

        LoginRequest req = LoginRequest.builder()
            .username("alice")
            .password("wrong")
            .build();

        BaseException ex = assertThrows(BaseException.class, () -> authService.login(req));
        assertEquals(ErrorCode.AUTH_ERROR, ex.getErrorCode());
        verify(redisRefreshToken, never()).save(any(UUID.class), anyString());
    }

    @DisplayName("리프레시: 저장된 refreshToken과 불일치하면 LP-AUTH 예외가 발생한다")
    @Test
    public void refresh_whenMismatch_thenAuthError() {
        UUID memberId = UUID.randomUUID();
        when(redisRefreshToken.load(memberId)).thenReturn(Optional.of("stored-refresh"));

        BaseException ex = assertThrows(
            BaseException.class,
            () -> authService.refresh(memberId.toString(), "alice", "wrong-refresh")
        );

        assertEquals(ErrorCode.AUTH_ERROR, ex.getErrorCode());
    }


}


























