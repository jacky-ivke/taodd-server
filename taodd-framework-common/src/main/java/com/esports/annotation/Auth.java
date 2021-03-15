package com.esports.annotation;

import java.lang.annotation.*;

/**
 * 安全认证处理
 *
 * @author jacky
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auth {

    /**
     * 是否需要认证令牌
     *
     * @return
     */
    boolean authentication() default true;

    /**
     * 是否需要验证签名
     *
     * @return
     */
    boolean sign() default true;

    /**
     * 是否需要验证IP黑名单
     *
     * @return
     */
    boolean domain() default true;

    /**
     * 是否需要验证用户是否被锁定
     */
    boolean userlocked() default false;

    /**
     * 是否需要验证用户请求重复
     *
     * @return
     */
    boolean repeat() default false;

    /**
     * 身份类别
     *
     * @return
     */
    String identity() default "member";

    /**
     * 是否需要分布式锁机制
     *
     * @return
     */
    DistributedLock sync() default @DistributedLock(enabled = false);

    /**
     * 是否需要服务限流
     *
     * @return
     */
    DynamicRateLimiter rateLimiter() default @DynamicRateLimiter(enabled = true);

    /**
     * 受否接口访问限制
     *
     * @return
     */
    VisitLimiter visitLimiter() default @VisitLimiter(enabled = true);

}
