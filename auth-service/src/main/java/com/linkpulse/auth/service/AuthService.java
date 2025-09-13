package com.linkpulse.auth.service;

import com.linkpulse.auth.domain.Member;
import com.linkpulse.auth.dto.request.LoginRequest;
import com.linkpulse.auth.dto.request.SignUpRequest;
import com.linkpulse.auth.dto.response.TokenResponse;
import com.linkpulse.auth.token.JwtTokenProvider;
import com.linkpulse.auth.token.RedisRefreshToken;
import com.linkpulse.error.BaseException;
import com.linkpulse.error.ErrorCode;
import com.linkpulse.repository.MemberRepository;
import jakarta.validation.Valid;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisRefreshToken redisRefreshToken;

    public String signUp(@Valid SignUpRequest req) {
        if (memberRepository.existsByUsername(req.getUsername())) {
            throw new BaseException(ErrorCode.CONFLICT, "이미 존재하는 유저명입니다.");
        }

        Member savedMember = memberRepository.save(
            Member.builder()
                .username(req.getUsername())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .nickName(req.getNickName())
                .build());

        return savedMember.getId().toString();          // String 반환
    }

    public TokenResponse login(@Valid LoginRequest req) {
        Member member = memberRepository.findByUsername(req.getUsername())
            .orElseThrow(() -> new BaseException(ErrorCode.AUTH_ERROR, "Invalid credentials."));

        boolean checked = passwordEncoder.matches(req.getPassword(), member.getPasswordHash());
        if (!checked) {
            throw new BaseException(ErrorCode.AUTH_ERROR, "Invalid credentials.");
        }

        String access = jwtTokenProvider.createAccessToken(member.getId(), member.getUsername());
        String refresh = UUID.randomUUID().toString();
        redisRefreshToken.save(member.getId(), refresh);

        return TokenResponse.builder()
            .accessToken(access)
            .refreshToken(refresh)
            .expiresInSeconds(900L)
            .build();
    }

    public TokenResponse refresh(String memberId, String username, String refreshToken) {
        UUID id = UUID.fromString(memberId);
        Optional<String> saved = redisRefreshToken.load(id);
        if (saved.isEmpty() || !saved.get().equals(refreshToken)) {
            throw new BaseException(ErrorCode.AUTH_ERROR, "유효하지 않은 refreshToken.");
        }

        String access = jwtTokenProvider.createAccessToken(id, username);
        return TokenResponse.builder()
            .accessToken(access)
            .refreshToken(refreshToken)
            .expiresInSeconds(900L)
            .build();
    }

    public void logout(String memberId) {
        UUID id = UUID.fromString(memberId);
        redisRefreshToken.delete(id);
    }

}
