package com.esports.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicRateLimiter {

    /**
     * 是否开启限流
     *
     * @return
     */
    boolean enabled() default true;

    /**
     * 服务限流保护，服务每秒允许的TPS（需评估单个服务所允许的最大TPS）
     *
     * @return
     */
    double permitsPerSecond() default 300.0;

    /**
     * 配置超时时间（配置将等待获取，不配置将直接返回）,单位毫秒
     *
     * @return
     */
    long timeout() default 100;

}
