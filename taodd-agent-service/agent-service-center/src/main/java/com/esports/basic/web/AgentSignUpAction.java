package com.esports.basic.web;

import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.basic.bean.db1.Agent;
import com.esports.basic.dto.LoginDto;
import com.esports.basic.service.AgentCenterManager;
import com.esports.constant.CommonCode;
import com.esports.constant.GlobalCode;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.IPUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = {"【代理管理系统-代理中心相关接口】"})
@RestController
public class AgentSignUpAction {

    @Autowired
    private AgentCenterManager agentCenterManager;

    @ApiOperation("代理登录接口")

    @RequestMapping(value = "/agent/login", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户账号", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "password", value = "登录密码", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "key", value = "验证码KEY", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "verifyCode", value = "验证码", paramType = "query", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 411, message = "请求失败，提交参数格式有误"),
            @ApiResponse(code = 405, message = "请求失败，验证码错误或者已过期"),
            @ApiResponse(code = 444, message = "请求失败，资料审核中"),
            @ApiResponse(code = 443, message = "请求失败，非代理身份,可加入合营计划或者联系管理员"),
            @ApiResponse(code = 402, message = "请求失败，账号或者密码错误")
    })

    @Auth(authentication = false, domain = true, userlocked = true, identity = "agent", rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper login(HttpServletResponse response, HttpServletRequest request, String username, String password, String key, String verifyCode) {

        Integer source = GlobalCode.getSource(request);
        String ip = IPUtils.getIpAddr(request);
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(verifyCode)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        Agent agent = agentCenterManager.findByUserName(username);
        if (null == agent) {
            return JsonWrapper.failureWrapper(ResultCode._448.getCode(), ResultCode._448.getMessage());
        }
        String account = agent.getAccount();
        boolean success = agentCenterManager.checkLoginErrorCount(account, ip);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._303.getCode(), ResultCode._303.getMessage());
        }
        success = agentCenterManager.checkAuthCode(key, verifyCode);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._405.getCode(), ResultCode._405.getMessage());
        }
        success = agentCenterManager.checkAgentAuditStatus(account);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._444.getCode(), ResultCode._444.getMessage());
        }
        success = agentCenterManager.checkIdentity(account);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._443.getCode(), ResultCode._443.getMessage());
        }
        success = agentCenterManager.checkLoginPwd(agent.getLoginPwd(), password);
        if (!success) {
            agentCenterManager.setLoginErrorCount(account, ip);
            return JsonWrapper.failureWrapper(ResultCode._402.getCode(), ResultCode._402.getMessage());
        }
        String client = request.getHeader("User-Agent");
        String token = agentCenterManager.login(account, client, source, IPUtils.ipToLong(ip));
        response.setHeader(CommonCode.TOKEN_KEY, token);
        LoginDto dto = agentCenterManager.region(account, ip);
        return JsonWrapper.successWrapper(dto, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("代理登录接口")

    @RequestMapping(value = "/agent/extMobilelogin", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "verifyCode", value = "验证码", paramType = "query", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 411, message = "请求失败，提交参数格式有误"),
            @ApiResponse(code = 431, message = "请求失败，手机号未注册"),
            @ApiResponse(code = 405, message = "请求失败，验证码错误或者已过期")
    })

    @Auth(authentication = false, domain = true, userlocked = true, identity = "agent", rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper login(HttpServletResponse response, HttpServletRequest request, String mobile, String verifyCode) {

        Integer source = GlobalCode.getSource(request);
        String ip = IPUtils.getIpAddr(request);
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(verifyCode)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        Agent agent = agentCenterManager.findByUserName(mobile);
        if (null == agent) {
            return JsonWrapper.failureWrapper(ResultCode._448.getCode(), ResultCode._448.getMessage());
        }
        String account = agent.getAccount();
        boolean success = agentCenterManager.checkLoginErrorCount(account, ip);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._303.getCode(), ResultCode._303.getMessage());
        }
        success = agentCenterManager.checkSmsCode(mobile, verifyCode);
        if (!success) {
            agentCenterManager.setLoginErrorCount(account, ip);
            return JsonWrapper.failureWrapper(ResultCode._405.getCode(), ResultCode._405.getMessage());
        }
        success = agentCenterManager.checkAgentAuditStatusByMobile(mobile);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._444.getCode(), ResultCode._444.getMessage());
        }
        success = agentCenterManager.checkIdentity(account);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._443.getCode(), ResultCode._443.getMessage());
        }
        String client = request.getHeader("User-Agent");
        String token = agentCenterManager.login(account, client, source, IPUtils.ipToLong(ip));
        response.setHeader(CommonCode.TOKEN_KEY, token);
        LoginDto dto = agentCenterManager.region(account, ip);
        return JsonWrapper.successWrapper(dto, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
