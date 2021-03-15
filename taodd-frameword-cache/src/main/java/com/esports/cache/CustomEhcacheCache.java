package com.esports.cache;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.Callable;

@Slf4j
@Data
public class CustomEhcacheCache implements Cache {

    private Cache cache;

    private String name;

    public CustomEhcacheCache() {

    }

    public CustomEhcacheCache(String name, EhCacheCacheManager ehCacheCacheManager) {
        this.name = name;
        this.cache = ehCacheCacheManager.getCache(name);
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper value = null;
        if (!ObjectUtils.isEmpty(key)) {
            value = this.cache.get(key);
            log.info("根据键{}获取到的缓存值{}", key, value);
        }
        return value;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        T value = null;
        if (!ObjectUtils.isEmpty(key)) {
            value = this.cache.get(key, type);
            log.info("根据键{}获取到的缓存值{}", key, value);
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
            this.cache.put(key, value);
        }
    }

    @Override
    public void evict(Object key) {
        if (!ObjectUtils.isEmpty(key)) {
            this.cache.evict(key);
        }
    }

    @Override
    public void clear() {
        this.cache.clear();
    }

    @Override
    public String getName() {
        return this.name;
    }
}
