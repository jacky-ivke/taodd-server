package com.esports.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    /**
     * 默认开启锁
     *
     * @return
     */
    boolean enabled() default true;

    /**
     * 锁的前缀
     *
     * @return
     */
    String lockedPrefix() default "request_lock:%s";

    /**
     * 锁自动释放时间
     *
     * @return
     */
    long expireTime() default 5000;

    /**
     * 锁获取失败,是否重试
     *
     * @return
     */
    boolean reTry() default true;

    /**
     * 锁是否需要判断超时
     *
     * @return
     */
    boolean overTime() default true;

    /**
     * 尝试超时时间
     *
     * @return
     */
    long timeOutMillis() default 1000;

}
