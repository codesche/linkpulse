package com.linkpulse.auth.config;

import com.linkpulse.auth.token.JwtTokenProvider;
import com.linkpulse.auth.token.RedisRefreshToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class AuthBeanConfig {

    @Bean
    public JwtTokenProvider jwtTokenProvider(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.issuer}") String issuer,
        @Value("${jwt.access-token-ttl-seconds}") long accessTtlSeconds
    ) {
        return new JwtTokenProvider(secret, issuer, accessTtlSeconds);
    }

    @Bean
    public RedisRefreshToken redisRefreshToken(
        StringRedisTemplate template,
        @Value("${jwt.refresh-token-ttl-seconds}") long ttl
    ) {
        return new RedisRefreshToken(template, ttl);
    }
}
