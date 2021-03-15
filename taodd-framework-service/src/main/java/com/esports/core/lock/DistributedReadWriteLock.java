package com.esports.core.lock;

import com.esports.cache.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * 分布式读写锁，带线程等待阻塞
 */
@Component
@Slf4j
public class DistributedReadWriteLock {

    @Autowired
    private RedisUtil redisUtil;

    public boolean lock(String key, String value) {
        return this.lock(key, value, 60000L, true, 0, false, 0L);
    }

    public boolean exists(String key, String value) {
        String lockKey = this.generatorKey(key, value);
        return redisUtil.hasKey(lockKey);
    }

    public boolean lock(String key, String value, long expireTime, boolean reTry, int reTryCount, boolean overTime,
                        long timeOutMillis) {
        String lockKey = this.generatorKey(key, value);
        boolean success = redisUtil.setnx(lockKey, value, expireTime);
        if (success) {
//			log.info("lock success; key:{}, value:{}", lockKey, value);
            return true;
        }
        if (!reTry) {
            return false;
        }
        if (overTime && timeOutMillis <= 0) {
            return false;
        }
        // 当获取失败后, sleep一段时间
        long sleepMillis = getSleepMillis(overTime, timeOutMillis);
        // sleep后重新获取锁
        try {
            Thread.sleep(sleepMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 当重复获取一个key,达到警告值时,记录日志
        if (reTryCount++ > 10) {
            log.info("lock get warning; key:{}, value:{}", lockKey, value);
        }
        return lock(key, value, expireTime, reTry, reTryCount, overTime, timeOutMillis);
    }

    public boolean unlock(String key, String value) {
        String lockKey = this.generatorKey(key, value);
        boolean success = redisUtil.delnx(lockKey, value);
//		log.info("unlock key:{}, status:{}", lockKey, success);
        return success;
    }

    public String generatorKey(String prefix, String param) {
        String key = "";
        if (StringUtils.isEmpty(prefix)) {
            prefix = "redis_lock:%s";
        }
        if (StringUtils.isEmpty(param)) {
            key = String.format(prefix, UUID.randomUUID());
        }
        key = String.format(prefix, param);
        return key;
    }

    private static long getSleepMillis(boolean needTimeOut, long timeOutMillis) {
        long sleepMillis = 300L;// 默认sleep时间,300毫秒
        if (needTimeOut) {
            timeOutMillis = timeOutMillis - sleepMillis;
            if (timeOutMillis < sleepMillis) {
                sleepMillis = timeOutMillis;
            }
        }
        return sleepMillis;
    }
}
