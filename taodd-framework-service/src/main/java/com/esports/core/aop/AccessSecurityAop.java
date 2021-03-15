package com.esports.core.aop;

import com.esports.annotation.Auth;
import com.esports.annotation.DistributedLock;
import com.esports.cache.RedisUtil;
import com.esports.constant.CommonCode;
import com.esports.constant.RedisCacheKey;
import com.esports.core.exception.MyException;
import com.esports.core.lock.DistributedReadWriteLock;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.IPUtils;
import com.esports.utils.JWT;
import com.esports.utils.SignUtils;
import com.esports.utils.SortUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Slf4j
@Aspect
@Component
@Order(1)
public class AccessSecurityAop extends AbstractBaseAop {

    private final Logger sysApiLogger = LoggerFactory.getLogger("callExternalApiLogger");

    @Autowired
    private JWT jwt;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private DistributedReadWriteLock redisLock;

    private static final Cache<String, Object> caches = CacheBuilder.newBuilder()
            // 最大缓存 1000 个
            .maximumSize(1000)
            // 设置写缓存后 5 秒钟过期
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build();

    private static final Map<String, RateLimiter> limitEntityMap = Maps.newConcurrentMap();

    private final String[] ignoreRequest = new String[]{"HEAD", "OPTIONS", "TRACE"};

    @Pointcut(" execution(* com.esports..*.web..*.*(..))")
    public void pointcut() {

    }

    @Before("pointcut()")
    public void beforeAdvice(JoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Method method = this.getMethod(joinPoint);
        Auth auth = method.getDeclaredAnnotation(Auth.class);
//        this.isSqlValidate(request);
        this.isTokenExpired(auth, request);
//        this.isVaildSign(auth, request);
//        this.isFrequentVisits(auth, request);
    }

    @Around("pointcut()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        long startTime = System.currentTimeMillis();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (Arrays.asList(ignoreRequest).contains(request.getMethod())) {
            return JsonWrapper.successWrapper(ResultCode._200.getCode());
        }
        Method method = this.getMethod(joinPoint);
        Auth auth = method.getDeclaredAnnotation(Auth.class);
        try {
            //在执行方法时，是否需要加锁
            this.isSyncLocke(auth, request);
            this.requestConfig(auth, request);
            result = joinPoint.proceed();
        } finally {
            //如果有锁机制，则需要释放锁
            this.releaseLock(auth, request);
            long runTime = System.currentTimeMillis() - startTime;
            this.log(request, joinPoint, result, runTime, sysApiLogger);
        }
        return result;
    }

    protected void requestConfig(Auth auth, HttpServletRequest request) {
        String token = request.getHeader(CommonCode.TOKEN_KEY);
        if (null == auth || StringUtils.isEmpty(token)) {
            return;
        }
        String account = jwt.getSubject(token);
        request.setAttribute(CommonCode.JWT_USERNAME, account);
    }

    protected void isSqlValidate(HttpServletRequest request) {
        Map<String, Object> paramsMap = SortUtils.getParameterMap(request);
        if (CollectionUtils.isEmpty(paramsMap)) {
            return;
        }
        String regex = ".*(select|update|union|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|drop|execute|[+]|%).*";
        Iterator<Map.Entry<String, Object>> entries = paramsMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            Object value = entry.getValue();
            String param = String.valueOf(value);
            boolean isMatch = Pattern.matches(regex, param.toLowerCase());
            if (isMatch) {
                log.info("【 您的页面请求中有违反安全规则元素存在：{}】", param);
                throw new MyException(ResultCode._505.getCode(), ResultCode._505.getMessage());
            }
        }
    }

    protected void isVaildSign(Auth auth, HttpServletRequest request) {
        if (null == auth || !auth.sign() || Arrays.asList(ignoreRequest).contains(request.getMethod())) {
           return;
        }
        String timestamp = request.getHeader(CommonCode.FAILURE_TIME_KEY);
        long time = !StringUtils.isEmpty(timestamp)? Long.parseLong(timestamp) : 0L;
        boolean isTimeOut = SignUtils.checkTimestamp(time, CommonCode.INF_FAILURE_TIME);
        if (!isTimeOut) {
            throw new MyException(ResultCode._502.getCode(), ResultCode._502.getMessage());
        }
        String originalSign = request.getHeader(CommonCode.SIGN_KEY);
        String params = SortUtils.getAllParams(request);
        boolean result = SignUtils.verifySign(params, originalSign);
        if (!result) {
            throw new MyException(ResultCode._503.getCode(), ResultCode._503.getMessage());
        }
    }

    protected void isTokenExpired(Auth auth, HttpServletRequest request) {
        String token = request.getHeader(CommonCode.TOKEN_KEY);
        if (null == auth || !auth.authentication() || Arrays.asList(ignoreRequest).contains(request.getMethod())) {
            return;
        }
        String account = jwt.getSubject(token);
        if (StringUtils.isEmpty(account)) {
            throw new MyException(ResultCode._408.getCode(), ResultCode._408.getMessage());
        }
        String onlineKey = String.format(RedisCacheKey.ONLINE_KEY, account);
        redisUtil.set(onlineKey, account, CommonCode.ONLINE_EXPRIRED_TIME);
    }

    protected void isFrequentVisits(Auth auth, HttpServletRequest request) {
        if (null == auth || !auth.visitLimiter().enabled() || Arrays.asList(ignoreRequest).contains(request.getMethod())) {
            return;
        }
        int visitLimit = auth.visitLimiter().visits() <= 0 ? CommonCode.REQUEST_API_NUMS : auth.visitLimiter().visits();
        long time = auth.visitLimiter().time() <= 0L ? CommonCode.REDIS_DEFAULT_EXPRISE : auth.visitLimiter().time();
        String ip = IPUtils.getIpAddr(request);
        String url = request.getRequestURI();
        String ipKey = String.format(RedisCacheKey.IP_CHECK_KEY, ip, url);
        Object object = redisUtil.hasKey(ipKey)? redisUtil.get(ipKey) : null;
        Integer count =  null != object? Integer.parseInt(object.toString()) : 0;
        if (null == count || count <= 0) {
            redisUtil.set(ipKey, 1, time);
        } else if (count > visitLimit) {
            log.info("【用户IP：{},访问地址：{},超过了在规定时间{}s内访问接口次数>={}】", ip, url, time, visitLimit);
            throw new MyException(ResultCode._506.getCode(), ResultCode._506.getMessage());
        } else {
            redisUtil.incr(ipKey, 1);
            redisUtil.expire(ipKey, time);
        }
    }

    protected void isSyncLocke(Auth auth, HttpServletRequest request) {
        String token = request.getHeader(CommonCode.TOKEN_KEY);
        if (null == auth || StringUtils.isEmpty(token)) {
            return;
        }
        DistributedLock disLock = auth.sync();
        boolean enabled = disLock.enabled();
        String prefix = disLock.lockedPrefix();
        long expireTime = disLock.expireTime();
        boolean reTry = disLock.reTry();
        boolean overTime = disLock.overTime();
        long timeOutMillis = disLock.timeOutMillis();
        if (!enabled) {
            return;
        }
        try {
            String account = jwt.getSubject(token);
            String lockKey = redisLock.generatorKey(prefix, account);
            redisLock.lock(lockKey, account, expireTime, reTry, 0, overTime, timeOutMillis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void releaseLock(Auth auth, HttpServletRequest request) {
        String token = request.getHeader(CommonCode.TOKEN_KEY);
        if (null == auth || StringUtils.isEmpty(token)) {
            return;
        }
        DistributedLock disLock = auth.sync();
        boolean enabled = disLock.enabled();
        String prefix = disLock.lockedPrefix();
        if (!enabled) {
            return;
        }
        try {
            String account = jwt.getSubject(token);
            String lockKey = redisLock.generatorKey(prefix, account);
            redisLock.unlock(lockKey, account);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
