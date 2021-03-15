package com.esports.cache;

import lombok.Data;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.util.Collection;

@Data
public class CustomEhRedisCacheManager implements CacheManager {

	EhCacheCacheManager ehCacheCacheManager;
	
	RedisCacheManager redisCacheManager;
	
	public CustomEhRedisCacheManager(EhCacheCacheManager ehCacheCacheManager, RedisCacheManager redisCacheManager) {
		this.ehCacheCacheManager = ehCacheCacheManager;
		this.redisCacheManager = redisCacheManager;
	}

	@Override
	public Cache getCache(String name) {
		return new CustomEhRedisCache(name, ehCacheCacheManager, redisCacheManager);
	}

	@Override
	public Collection<String> getCacheNames() {
		return ehCacheCacheManager.getCacheNames();
	}
	
	
}
