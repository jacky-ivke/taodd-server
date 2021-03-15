package com.esports.com.esports.security.rpc;

import com.esports.com.esports.security.code.service.SecurityCodeManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"【系统组件-验证码、短信、邮件发送提供者】"})
@RestController
public class AuthcodeServiceRpcAction {

    @Autowired
    private SecurityCodeManager securityCodeManager;

    @ApiOperation("【内部模块依赖暴露接口】检验普通验证码")
    @RequestMapping(value = "/security/authcode", method = RequestMethod.GET)
    boolean checkAuthCode(String key, String verifyCode) {
        boolean result = securityCodeManager.checkCode(key, verifyCode);
        return result;
    }

    @ApiOperation("【内部模块依赖暴露接口】检验短信验证码")
    @RequestMapping(value = "/security/smscode", method = RequestMethod.GET)
    boolean checkSmsCode(String mobile, String verifyCode) {
        boolean result = securityCodeManager.checkSmsCode(mobile, verifyCode);
        return result;
    }

    @ApiOperation("【内部模块依赖暴露接口】检验邮箱验证码")
    @RequestMapping(value = "/security/emailcode", method = RequestMethod.GET)
    boolean checkEmailCode(String email, String verifyCode) {
        boolean result = securityCodeManager.checkEmailCode(email, verifyCode);
        return result;
    }
}
