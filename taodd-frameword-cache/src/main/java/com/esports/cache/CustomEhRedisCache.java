package com.esports.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.Callable;

@Slf4j
public class CustomEhRedisCache implements Cache {

    private final String name;

    private final Cache ehCache;

    private final Cache redisCache;

    public CustomEhRedisCache(String name, EhCacheCacheManager ehCacheCacheManager, RedisCacheManager redisCacheManager) {
        this.name = name;
        this.ehCache = this.getEhCache(name, ehCacheCacheManager);
        this.redisCache = this.getRedisCache(name, redisCacheManager);
    }

    public Cache getEhCache(String name, EhCacheCacheManager ehCacheCacheManager) {
        String cacheName = "";
        String arr[] = name.split("&");
        if (null != arr && arr.length > 0) {
            cacheName = arr[0];
        } else {
            cacheName = name;
        }
        return ehCacheCacheManager.getCache(cacheName);
    }

    public Cache getRedisCache(String name, RedisCacheManager redisCacheManager) {
        String cacheName = "";
        String arr[] = name.split("&");
        if (null != arr && arr.length > 1) {
            cacheName = arr[1];
        } else {
            cacheName = name;
        }
        return redisCacheManager.getCache(cacheName);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper value = null;
        if (!ObjectUtils.isEmpty(key)) {
            value = this.ehCache.get(key);
            if (ObjectUtils.isEmpty(value)) {
                value = redisCache.get(key);
                if (!ObjectUtils.isEmpty(value)) {
                    this.redisCache.put(key, value);
//					log.info("根据键{}获取到的二级缓存的值{}，并将二级缓存值存入一级缓存", key, value);
                }
            } else {
//				log.info("根据键{}获取到的缓存值{}", key, value);
            }
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object key, Class<T> type) {
        T value = null;
        if (!ObjectUtils.isEmpty(key)) {
            value = this.ehCache.get(key, type);
            if (ObjectUtils.isEmpty(value)) {
                value = (T) redisCache.get(key);
                if (!ObjectUtils.isEmpty(value)) {
                    this.redisCache.put(key, value);
//					log.info("根据键{}获取到的二级缓存的值{}，并将二级缓存值存入一级缓存", key, value);
                }
            } else {
//				log.info("根据键{}获取到的缓存值{}", key, value);
            }
        }
        return value;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return null;
    }

    @Override
    public void put(Object key, Object value) {
        log.info("即将缓存键{},缓存值{}", key, value);
        if (!ObjectUtils.isEmpty(key) && !ObjectUtils.isEmpty(value)) {
            this.ehCache.put(key, value);
            this.redisCache.put(key, value);
        }
    }

    @Override
    public void evict(Object key) {
        if (!ObjectUtils.isEmpty(key)) {
            this.ehCache.evict(key);
            this.redisCache.evict(key);
        }
    }

    @Override
    public void clear() {
        this.ehCache.clear();
        this.redisCache.clear();
    }

}
