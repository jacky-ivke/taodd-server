package com.esports.core.aop;

import com.esports.utils.IPUtils;
import com.esports.utils.JsonUtil;
import com.esports.utils.SortUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public abstract class AbstractBaseAop {

    protected String getMethodName(JoinPoint joinPoint) {
        Class<?> classTarget = joinPoint.getTarget().getClass();
        String methodName = joinPoint.getSignature().getName();
        Class<?>[] par = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();
        String method = "";
        try {
            method = classTarget.getMethod(methodName, par).getName();
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return method;
    }

    protected String getClassName(JoinPoint joinPoint) {
        Class<?> classTarget = joinPoint.getTarget().getClass();
        String objClass = classTarget.getName();
        return objClass;
    }

    protected Method getMethod(JoinPoint joinPoint) {
        Class<?> classTarget = joinPoint.getTarget().getClass();
        String methodName = joinPoint.getSignature().getName();
        Class<?>[] par = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();
        Method method = null;
        try {
            method = classTarget.getMethod(methodName, par);
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return method;
    }

    protected Class<?> getClass(JoinPoint joinPoint) {
        Class<?> classTarget = joinPoint.getTarget().getClass();
        return classTarget;
    }

    public void log(HttpServletRequest request, JoinPoint joinPoint, Object result, Long runTime, Logger sysApiLogger) {
        String ip = IPUtils.getIpAddr(request);
        String url = String.valueOf(request.getRequestURL());
        String args = JsonUtil.map2String(SortUtils.getParameterMap(request));
        String method = request.getMethod();
        sysApiLogger.info("===================【{}】==================", this.getClassName(joinPoint));
        sysApiLogger.info("请求IP：{}", ip);
        sysApiLogger.info("请求来源：{}", url);
        sysApiLogger.info("请求方式：{}", method);
        sysApiLogger.info("请求方法：{}", this.getMethodName(joinPoint));
        sysApiLogger.info("请求参数：{}", args);
        sysApiLogger.info("执行时间：{} ms", runTime);
        sysApiLogger.info("返回结果：{}", result);
    }
}
