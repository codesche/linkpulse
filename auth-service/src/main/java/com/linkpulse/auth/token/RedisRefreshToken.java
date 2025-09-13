package com.linkpulse.auth.token;

import com.linkpulse.token.RefreshTokenStore;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@RequiredArgsConstructor
public class RedisRefreshToken implements RefreshTokenStore {

    private final StringRedisTemplate redis;
    private final long refreshTtlSeconds;

    public void save(UUID memberId, String refreshToken) {
        ValueOperations<String, String> ops = redis.opsForValue();
        ops.set(key(memberId), refreshToken, Duration.ofSeconds(refreshTtlSeconds));
    }

    public Optional<String> load(UUID memberId) {
        String value = redis.opsForValue().get(key(memberId));
        return Optional.ofNullable(value);
    }

    public void delete(UUID memberId) {
        redis.delete(key(memberId));
    }

    private String key(UUID memberId) {
        return "auth:refresh:" + memberId;
    }

}
