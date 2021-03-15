package com.esports.core.config;

import com.esports.core.exception.MyException;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionConfig {

    /**
     * 全局异常处理
     */
    @ExceptionHandler(value = Exception.class)
    public JsonWrapper exceptionHandler(Exception e) {
        log.error("【全局异常处理器】异常信息：{}", e.getMessage());
        return JsonWrapper.failureWrapper(ResultCode._500.getCode(), e.getMessage());
    }

    /**
     * 自定义异常
     */
    @ExceptionHandler(value = MyException.class)
    public JsonWrapper exceptionHandler(MyException e) {
        log.error("【自定义异常处理器】异常代码：{}， 异常信息：{}，返回结果：{}", e.getCode(), e.getMsg(), e.getData());
        return JsonWrapper.failureWrapper(e.getData(), e.getCode(), e.getMsg());
    }

}
