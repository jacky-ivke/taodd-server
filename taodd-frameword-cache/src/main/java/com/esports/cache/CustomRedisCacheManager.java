package com.esports.cache;

import lombok.Data;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.util.Collection;

@Data
public class CustomRedisCacheManager implements CacheManager {

    RedisCacheManager redisCacheManager;

    public CustomRedisCacheManager(RedisCacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }

    @Override
    public Cache getCache(String name) {
        return new CustomRedisCache(name, this.redisCacheManager);
    }

    @Override
    public Collection<String> getCacheNames() {
        return redisCacheManager.getCacheNames();
    }

}
