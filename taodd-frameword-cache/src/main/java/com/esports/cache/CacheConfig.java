package com.esports.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Configuration
public class CacheConfig{

    /**
     * cacheManager名字
     */
    public interface CacheManagerNames {
        /**
         * redis
         */
        String REDIS_CACHE_MANAGER = "customRedisCacheManager";

        /**
         * ehCache
         */
        String EHCACHE_CACHE_MAANGER = "customEhCacheCacheManager";

        /**
         * redis+ehCache
         */
        String EHCACHE_REDIS_CACHE_MAANGER = "customEhRedisCacheManager";
    }

    public interface EhRedisCacheNames {
        /**
         * 5-15分钟缓存组
         */
        String CACHE_15MINS = "mycache:cache:5m&mycache:cache:15m";

        /**
         * 5-30分钟缓存组
         */
        String CACHE_30MINS = "mycache:cache:5mm&mycache:cache:30m";

        /**
         * 5-60分钟缓存组
         */
        String CACHE_60MINS = "mycache:cache:5m&mycache:cache:60m";
    }

    public interface RedisCacheNames {
        /**
         * 15分钟缓存组
         */
        String CACHE_15MINS = "mycache:cache:15m";
        /**
         * 30分钟缓存组
         */
        String CACHE_30MINS = "mycache:cache:30m";
        /**
         * 60分钟缓存组
         */
        String CACHE_60MINS = "mycache:cache:60m";
        /**
         * 180分钟缓存组
         */
        String CACHE_180MINS = "mycache:cache:180m";
    }

    public interface EhCacheNames {

        String CACHE_5INS = "mycache:cache:5m";

        String CACHE_10MINS = "mycache:cache:10m";

        String CACHE_20MINS = "mycache:cache:20m";

        String CACHE_30MINS = "mycache:cache:30m";
    }

    @Primary
    @Bean
    public CacheManager customEhCacheCacheManager(
            @Qualifier("ehCacheCacheManager") EhCacheCacheManager ehCacheCacheManager) {
        CustomCacheManager ehCacheManager = new CustomCacheManager(ehCacheCacheManager);
        return ehCacheManager;
    }

    @Bean
    public CacheManager customRedisCacheManager(@Qualifier("redisCacheManager") RedisCacheManager redisCacheManager) {
        CustomRedisCacheManager customRedisCacheManager = new CustomRedisCacheManager(redisCacheManager);
        return customRedisCacheManager;
    }

    @Bean
    public CacheManager customEhRedisCacheManager(@Qualifier("redisCacheManager") RedisCacheManager redisCacheManager,
                                                  @Qualifier("ehCacheCacheManager") EhCacheCacheManager ehCacheCacheManager) {
        CustomEhRedisCacheManager customEhRedisCacheManager = new CustomEhRedisCacheManager(ehCacheCacheManager, redisCacheManager);
        return customEhRedisCacheManager;
    }

    @Bean(name = "ehCacheCacheManager")
    public EhCacheCacheManager ehCacheCacheManager() {
        EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager(
                EhCacheManagerUtils.buildCacheManager());
        return ehCacheCacheManager;
    }

    @Bean(name = "redisCacheManager")
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        RedisSerializer<Object> jackson2JsonRedisSerializer = this.jackson2JsonRedisSerializer();

        // 生成一个默认配置，通过config对象即可对缓存进行自定义配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        config = config.entryTtl(Duration.ofMinutes(30)) // 设置缓存的默认过期时间，也是使用Duration设置, 设置为30分钟
                .disableCachingNullValues(); // 不缓存空值

        Set<String> cacheNames = new HashSet<>();
        cacheNames.add(RedisCacheNames.CACHE_15MINS);
        cacheNames.add(RedisCacheNames.CACHE_30MINS);
        cacheNames.add(RedisCacheNames.CACHE_60MINS);
        cacheNames.add(RedisCacheNames.CACHE_180MINS);

        // 对每个缓存空间应用不同的配置
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>(16);
        configMap.put(RedisCacheNames.CACHE_15MINS, config.entryTtl(Duration.ofMinutes(15))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringRedisSerializer))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer)));

        configMap.put(RedisCacheNames.CACHE_30MINS, config.entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringRedisSerializer))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer)));

        configMap.put(RedisCacheNames.CACHE_60MINS, config.entryTtl(Duration.ofMinutes(60))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringRedisSerializer))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer)));

        configMap.put(RedisCacheNames.CACHE_180MINS, config.entryTtl(Duration.ofMinutes(180))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringRedisSerializer))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer)));

        // 配置序列化（解决乱码的问题）,过期时间30秒
        RedisCacheConfiguration redisCacheConfiguration = config
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringRedisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer));
        RedisCacheManager cacheManager = RedisCacheManager.builder(connectionFactory).initialCacheNames(cacheNames)
                .withInitialCacheConfigurations(configMap).cacheDefaults(redisCacheConfiguration).transactionAware()
                .build();
        return cacheManager;
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
        redisTemplate.setConnectionFactory(connectionFactory);
        RedisSerializer<Object> jackson2JsonRedisSerializer = this.jackson2JsonRedisSerializer();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        // 开启事务(开启后,redistemplate hasKey会出现空指针异常,此处异常不能这样使用,请注意)
        //redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }


    @Bean
    public Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
        // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
                Object.class);
        // 解决查询缓存转换异常的问题
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(mapper);
        return jackson2JsonRedisSerializer;
    }
}
