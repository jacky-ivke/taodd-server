package com.esports.cache;

import lombok.Data;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import java.util.Collection;

@Data
public class CustomCacheManager implements CacheManager {

    EhCacheCacheManager ehCacheCacheManager;

    public CustomCacheManager(EhCacheCacheManager ehCacheCacheManager) {
        this.ehCacheCacheManager = ehCacheCacheManager;
    }

    @Override
    public Cache getCache(String name) {
        return new CustomEhcacheCache(name, ehCacheCacheManager);
    }

    @Override
    public Collection<String> getCacheNames() {
        return ehCacheCacheManager.getCacheNames();
    }


}
