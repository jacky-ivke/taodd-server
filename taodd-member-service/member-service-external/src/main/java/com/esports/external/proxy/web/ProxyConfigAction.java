package com.esports.external.proxy.web;

import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.external.handler.xint.tfg.XintTfgProxy;
import com.esports.response.ResultCode;
import com.esports.utils.SortUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Api(tags = {"【会员管理系统-第三方应用相关接口】"})
@RestController
public class ProxyConfigAction {

    @ApiOperation("雷火电竞应用token认证回调接口")
    @RequestMapping(value = "token/validate", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})
    @Auth(authentication = false, domain = false, sign = false, userlocked = false, rateLimiter = @DynamicRateLimiter(enabled = true))
    public void checkToken(HttpServletResponse response, HttpServletRequest request) {
        Map<String, Object> bodyMap = SortUtils.getApplicationJsonData(request);
        if (CollectionUtils.isEmpty(bodyMap)) {
            response.setStatus(ResultCode._400.getCode());
            return;
        }
        String token = null != bodyMap.get("token") ? bodyMap.get("token").toString() : "";
        if (StringUtils.isEmpty(token)) {
            response.setStatus(ResultCode._400.getCode());
            return;
        }
        try {
            String loginName = XintTfgProxy.getSubject(token);
            JSONObject json = new JSONObject();
            json.put("loginName", loginName);
            response.getWriter().write(json.toString());
        } catch (IOException e) {
            response.setStatus(ResultCode._400.getCode());
            return;
        }
    }
}
