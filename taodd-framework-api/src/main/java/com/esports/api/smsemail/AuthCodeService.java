package com.esports.api.smsemail;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "taodd-deploy", path = "/v1.0")
public interface AuthCodeService {

    /**
     * 校验验证码是否正确
     */
    @RequestMapping(value = "/security/authcode", method = RequestMethod.GET)
    boolean checkAuthCode(@RequestParam("key") String key, @RequestParam("verifyCode") String verifyCode);

    /**
     * 校验短信验证码是否正确
     */
    @RequestMapping(value = "/security/smscode", method = RequestMethod.GET)
    boolean checkSmsCode(@RequestParam("mobile") String mobile, @RequestParam("verifyCode") String verifyCode);

    /**
     * 校验邮箱验证码是否正确
     */
    @RequestMapping(value = "/security/emailcode", method = RequestMethod.GET)
    boolean checkEmailCode(@RequestParam("email") String email, @RequestParam("verifyCode") String verifyCode);
}
