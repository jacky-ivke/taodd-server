package com.esports.com.esports.security.code.web;


import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.com.esports.security.code.service.SecurityCodeManager;
import com.esports.constant.SmsCode;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.RandomUtil;
import io.swagger.annotations.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = {"【系统组件-验证码安全相关接口】"})
@RestController
public class SecurityCodeAction {

    @Autowired
    private SecurityCodeManager securityCodeManager;

    @ApiOperation("获取图片验证码")

    @RequestMapping(value = "getImgCode", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getImgCode(HttpServletResponse response, HttpServletRequest request) {

        String key = RandomUtil.getUUID("");
        JSONObject json = securityCodeManager.getImgBase64(key);
        return JsonWrapper.successWrapper(json, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取数字验证码")

    @RequestMapping(value = "getCode", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getCode(HttpServletResponse response, HttpServletRequest request) {

        String key = RandomUtil.getUUID("");
        JSONObject json = securityCodeManager.getCode(key);
        return JsonWrapper.successWrapper(json, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("发送短信验证码")

    @RequestMapping(value = "sendSmsCode", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "验证码类型，预留字段，当前可忽略(0、不需验证手机;1、校验是否是绑定手机;2、验证手机是否已经使用)", paramType = "query", dataType = "Integer", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 411, message = "请求失败，请求参数不合法"),
            @ApiResponse(code = 431, message = "请求失败，非用户绑定手机"),
            @ApiResponse(code = 420, message = "请求失败，验证码发送过于频繁"),
            @ApiResponse(code = 422, message = "请求失败，当前手机号今日内发送验证码数量已超出限制"),
            @ApiResponse(code = 500, message = "服务器错误")
    })

    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper sendSmsCode(HttpServletResponse response, HttpServletRequest request, String mobile, Integer type) {

        if (StringUtils.isEmpty(mobile)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        Integer result = securityCodeManager.isAuthCodeSend(mobile);
        if (SmsCode._FREQUENT_SENDING.getCode().equals(result)) {
            return JsonWrapper.failureWrapper(ResultCode._420.getCode(), ResultCode._420.getMessage());
        }
        if (SmsCode._FREQUENCY_LIMIT.getCode().equals(result)) {
            return JsonWrapper.failureWrapper(ResultCode._422.getCode(), ResultCode._422.getMessage());
        }
        if (SmsCode._FREQUENCY_NORMAL.getCode().equals(result)) {
            String smsCode = securityCodeManager.sendSmsCode(mobile);
            return JsonWrapper.successWrapper(smsCode, ResultCode._200.getCode(), ResultCode._200.getMessage());
        }
        return JsonWrapper.failureWrapper(ResultCode._500.getCode(), ResultCode._500.getMessage());
    }

    @ApiOperation("发送邮件验证码")

    @RequestMapping(value = "sendEmailCode", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "邮箱", paramType = "query", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 411, message = "请求失败，请求参数不合法")
    })

    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper sendEmailCode(HttpServletResponse response, HttpServletRequest request, String email) {

        if (StringUtils.isEmpty(email)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        String emailCode = securityCodeManager.sendEmailCode(email);
        return JsonWrapper.successWrapper(emailCode, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
