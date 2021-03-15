package com.esports.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.Callable;

@Slf4j
public class CustomRedisCache implements Cache{

	private final Cache cache;
	
	private final String name;
	
	public CustomRedisCache(String name, RedisCacheManager redisCacheManager){
		this.name = name;
		this.cache = redisCacheManager.getCache(name);
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
		if(!ObjectUtils.isEmpty(key)) {
			value =this.cache.get(key);
//			log.info("根据键{}获取到的缓存值{}", key, JSONObject.toJSONString(value));
			if(!ObjectUtils.isEmpty(value)) {
				return value;
			}
		}
		return value;
	}

	@Override
	public <T> T get(Object key, Class<T> type) {
		T value = null;
		if(!ObjectUtils.isEmpty(key)) {
			value = this.cache.get(key, type);
//			log.info("根据键{}获取到的缓存值{}", key, value);
		}
		return value;
	}

	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		return null;
	}

	@Override
	public void put(Object key, Object value) {
//		log.info("即将缓存键{},缓存值{}", key, value);
		if(!ObjectUtils.isEmpty(key) && !ObjectUtils.isEmpty(value)) {
			this.cache.put(key, value);
		}
	}

	@Override
	public void evict(Object key) {
		if(!ObjectUtils.isEmpty(key)) {
			this.cache.evict(key);
		}
	}

	@Override
	public void clear() {
		this.cache.clear();
	}
	
}
