package com.esports.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VisitLimiter {

    /**
     * 是否开启限流
     *
     * @return
     */
    boolean enabled() default true;

    /**
     * 服务接口限流保护，服务接口3秒内访问次数不能超过次
     *
     * @return
     */
    int visits() default 30;

    /**
     * 配置时间范围
     *
     * @return
     */
    long time() default 3;

}
