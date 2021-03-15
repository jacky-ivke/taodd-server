package com.esports.monitor;

import com.alibaba.fastjson.JSONObject;
import com.esports.annotation.Auth;
import com.esports.api.center.MemberService;
import com.esports.api.interest.MemberInterestService;
import com.esports.basic.service.IpBlacklistManager;
import com.esports.cache.RedisUtil;
import com.esports.constant.CommonCode;
import com.esports.constant.GlobalCode;
import com.esports.constant.RedisCacheKey;
import com.esports.core.aop.AbstractBaseAop;
import com.esports.core.exception.MyException;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.IPUtils;
import com.esports.utils.JWT;
import com.esports.utils.JsonUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class MonitorHandlerAop extends AbstractBaseAop {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private JWT jwt;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberInterestService memberInterestService;

    @Autowired
    private IpBlacklistManager ipBlacklistManager;

    private final String[] ignoreRequest = new String[]{"HEAD", "OPTIONS", "TRACE"};


    @Pointcut(" execution(* com.esports..*.web..*.*(..))")
    public void pointcut() {

    }

    @Before("pointcut()")
    public void beforeAdvice(JoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Method method = this.getMethod(joinPoint);
        Auth auth = method.getDeclaredAnnotation(Auth.class);
        this.isSso(auth, request);
        this.isBlacklistIp(auth, request);
    }

    public void isSso(Auth auth, HttpServletRequest request) {
        if (null == auth || !auth.authentication() || Arrays.asList(ignoreRequest).contains(request.getMethod())) {
            return;
        }
        String token = request.getHeader(CommonCode.TOKEN_KEY);
        String account = jwt.getSubject(token);
        String redisKey = String.format(RedisCacheKey.MEMBER_SSO_KEY, account);
        //此处写法容易造成异常
        //String redsiToken = redisUtil.hasKey(redisKey) && null != redisUtil.get(redisKey) ? redisUtil.get(redisKey).toString() : "";
        Object object = redisUtil.hasKey(redisKey) ? redisUtil.get(redisKey) : null;
        String redsiToken = null != object ? object.toString() : "";
        if (!StringUtils.isEmpty(token) && !redsiToken.equals(token)) {
            throw new MyException(ResultCode._408.getCode(), ResultCode._408.getMessage());
        }
    }

    protected void isBlacklistIp(Auth auth, HttpServletRequest request) {
        if (null == auth || !auth.authentication() || Arrays.asList(ignoreRequest).contains(request.getMethod())) {
            return;
        }
        if (!auth.domain()) {
            return;
        }
        String ip = IPUtils.getIpAddr(request);
        boolean result = ipBlacklistManager.checkIpForbidden(ip);
        if (result) {
            throw new MyException(ResultCode._407.getCode(), ResultCode._407.getMessage());
        }
    }

    @Around("pointcut()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        result = this.changeResultValue(request, result);
        return result;
    }

    protected JsonWrapper changeResultValue(HttpServletRequest request, Object result) {
        String token = request.getHeader(CommonCode.TOKEN_KEY);
        String json = JsonUtil.object2String(result);
        JsonWrapper jsonWrapper = JsonUtil.string2Object(json, JsonWrapper.class);
        if (StringUtils.isEmpty(token)) {
            return jsonWrapper;
        }
        String account = jwt.getSubject(token);
        String ip = IPUtils.getIpAddr(request);
        Integer source = GlobalCode.getSource(request);
        String extData = memberService.getExtData(account, source, ip);
        jsonWrapper.setExtParams(StringUtils.isEmpty(extData) ? null : JSONObject.parse(extData));
        this.autoReceive(account, request);
        return jsonWrapper;
    }

    //利息钱包特殊业务逻辑处理
    protected void autoReceive(String account, HttpServletRequest request) {
        if (StringUtils.isEmpty(account)) {
            return;
        }
        try {
            String ip = IPUtils.getIpAddr(request);
            memberInterestService.autoReceive(account, ip);
        } catch (Exception ex) {
        }
    }
}
