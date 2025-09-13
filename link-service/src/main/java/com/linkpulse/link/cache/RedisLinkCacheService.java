package com.linkpulse.link.cache;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisLinkCacheService implements LinkCacheService{

    private final StringRedisTemplate redisTemplate;

    @Value("${link.cache.ttl-seconds:86400}")
    private long ttlSeconds;

    private String key(String code) {
        return "link:code" + code;
    }

    @Override
    public String getUrlByCode(String shortCode) {
        return redisTemplate.opsForValue().get(key(shortCode));
    }

    @Override
    public void putUrlByCode(String shortCode, String url) {
        redisTemplate.opsForValue().set(key(shortCode), url, Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public void evict(String shortCode) {
        redisTemplate.delete(key(shortCode));
    }
}
