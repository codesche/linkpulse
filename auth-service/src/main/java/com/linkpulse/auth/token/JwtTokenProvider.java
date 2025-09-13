package com.linkpulse.auth.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;

public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final String issuer;
    private final long accessTtlSeconds;

    public JwtTokenProvider(String secret, String issuer, long accessTtlSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.accessTtlSeconds = accessTtlSeconds;
    }

    public String createAccessToken(UUID memberId, String username) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessTtlSeconds);
        return Jwts.builder()
            .issuer(this.issuer)
            .subject(memberId.toString())
            .claims(Map.of("username", username, "scope", "USER"))
            .issuedAt(Date.from(now))
            .expiration(Date.from(exp))
            .signWith(this.secretKey)
            .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
            .verifyWith(this.secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

}
