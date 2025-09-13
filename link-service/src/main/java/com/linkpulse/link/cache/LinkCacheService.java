package com.linkpulse.link.cache;

public interface LinkCacheService {
    String getUrlByCode(String shortCode);
    void putUrlByCode(String shortCode, String url);
    void evict(String shortCode);
}
