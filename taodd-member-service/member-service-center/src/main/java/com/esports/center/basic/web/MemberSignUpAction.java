package com.esports.center.basic.web;

import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.center.basic.bean.db1.Member;
import com.esports.center.basic.dto.LoginDto;
import com.esports.center.basic.service.MemberCenterManager;
import com.esports.center.basic.web.vo.PasswordVo;
import com.esports.center.basic.web.vo.RegisterAccountMobileVo;
import com.esports.constant.CommonCode;
import com.esports.constant.GlobalCode;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.IPUtils;
import com.esports.utils.RegxUtils;
import io.jsonwebtoken.lang.Assert;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = {"【会员管理系统-会员中心相关接口】"})
@RestController
public class MemberSignUpAction {

    @Autowired
    private MemberCenterManager memberCenterManager;

    @ApiOperation("登录会员服务系统")

    @RequestMapping(value = "login", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "会员账号", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "password", value = "登录密码", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "code", value = "验证码", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "key", value = "验证码KEY", paramType = "query", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 303, message = "请求失败,连续登录错误超过5次，今日登录受限"),
            @ApiResponse(code = 405, message = "请求失败,验证码已过期"),
            @ApiResponse(code = 403, message = "用户被锁定"),
            @ApiResponse(code = 402, message = "登录账号或者密码错误")
    })

    @Auth(authentication = false, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper login(HttpServletResponse response, HttpServletRequest request, String username, String password, String code, String key) {

        Integer source = GlobalCode.getSource(request);
        String ip = IPUtils.getIpAddr(request);
        if (!RegxUtils.checkAccount(username)) {
            return JsonWrapper.failureWrapper(ResultCode._300.getCode(), ResultCode._300.getMessage());
        }
        if (!RegxUtils.checkPassword(password)) {
            return JsonWrapper.failureWrapper(ResultCode._301.getCode(), ResultCode._301.getMessage());
        }
        if (StringUtils.isEmpty(code) || StringUtils.isEmpty(key)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        //username有可能是账号或者手机号，统一转换成账号
        Member member = memberCenterManager.findByUserName(username);
        if (null == member) {
            return JsonWrapper.failureWrapper(ResultCode._448.getCode(), ResultCode._448.getMessage());
        }
        String account = member.getAccount();
        boolean success = memberCenterManager.checkLoginErrorCount(account, ip);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._303.getCode(), ResultCode._303.getMessage());
        }
        success = memberCenterManager.checkAuthCode(key, code);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._405.getCode(), ResultCode._405.getMessage());
        }
        success = memberCenterManager.checkLoginPwd(member, password);
        if (!success) {
            memberCenterManager.setLoginErrorCount(account, ip);
            return JsonWrapper.failureWrapper(ResultCode._402.getCode(), ResultCode._402.getMessage());
        }
        String client = request.getHeader("User-Agent");
        String token = memberCenterManager.login(account, client, source, IPUtils.ipToLong(ip));
        response.setHeader(CommonCode.TOKEN_KEY, token);
        LoginDto dto = memberCenterManager.region(account, ip);
        return JsonWrapper.successWrapper(dto, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("登录会员服务系统")

    @RequestMapping(value = "extMobilelogin", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "会员账号", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "password", value = "登录密码", paramType = "query", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "返回访问令牌"),
            @ApiResponse(code = 400, message = "请求语法错误"),
            @ApiResponse(code = 500, message = "服务错误"),
            @ApiResponse(code = 405, message = "验证码已过期"),
            @ApiResponse(code = 403, message = "用户被锁定"),
            @ApiResponse(code = 407, message = "域名访问限制"),
            @ApiResponse(code = 402, message = "登录账号或者密码错误"),
            @ApiResponse(code = 411, message = "用户名或密码格式不对")})

    @Auth(authentication = false, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper extMobilelogin(HttpServletResponse response, HttpServletRequest request, String username, String password) {

        Integer source = GlobalCode.getSource(request);
        String ip = IPUtils.getIpAddr(request);
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        //username有可能是账号或者手机号，统一转换成账号
        Member member = memberCenterManager.findByUserName(username);
        if (null == member) {
            return JsonWrapper.failureWrapper(ResultCode._402.getCode(), ResultCode._402.getMessage());
        }
        String account = member.getAccount();
        boolean success = memberCenterManager.checkLoginErrorCount(account, ip);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._303.getCode(), ResultCode._303.getMessage());
        }
        success = memberCenterManager.checkSmsCode(username, password);
        if (!success) {
            memberCenterManager.setLoginErrorCount(account, ip);
            return JsonWrapper.failureWrapper(ResultCode._402.getCode(), ResultCode._402.getMessage());
        }
        String client = request.getHeader("User-Agent");
        String token = memberCenterManager.login(account, client, source, IPUtils.ipToLong(ip));
        response.setHeader(CommonCode.TOKEN_KEY, token);
        LoginDto dto = memberCenterManager.region(account, ip);
        return JsonWrapper.successWrapper(dto, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("登录会员服务系统")

    @RequestMapping(value = "extMobileCodelogin", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "手机号", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "password", value = "手机验证码", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "code", value = "数字验证", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "key", value = "验证码KEY", paramType = "query", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "返回访问令牌"),
            @ApiResponse(code = 400, message = "请求语法错误"),
            @ApiResponse(code = 500, message = "服务错误"),
            @ApiResponse(code = 405, message = "验证码已过期"),
            @ApiResponse(code = 403, message = "用户被锁定"),
            @ApiResponse(code = 407, message = "域名访问限制"),
            @ApiResponse(code = 402, message = "登录账号或者密码错误"),
            @ApiResponse(code = 411, message = "用户名或密码格式不对")})

    @Auth(authentication = false, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper extMobileAndCodelogin(HttpServletResponse response, HttpServletRequest request, String username, String password, String code, String key) {

        Integer source = GlobalCode.getSource(request);
        String ip = IPUtils.getIpAddr(request);
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(code)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        //username有可能是账号或者手机号，统一转换成账号
        Member member = memberCenterManager.findByUserName(username);
        if (null == member) {
            return JsonWrapper.failureWrapper(ResultCode._402.getCode(), ResultCode._402.getMessage());
        }
        String account = member.getAccount();
        boolean success = memberCenterManager.checkLoginErrorCount(account, ip);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._303.getCode(), ResultCode._303.getMessage());
        }
        success = memberCenterManager.checkAuthCode(key, password);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._405.getCode(), ResultCode._405.getMessage());
        }
        success = memberCenterManager.checkSmsCode(username, password);
        if (!success) {
            memberCenterManager.setLoginErrorCount(account, ip);
            return JsonWrapper.failureWrapper(ResultCode._402.getCode(), ResultCode._402.getMessage());
        }
        String client = request.getHeader("User-Agent");
        String token = memberCenterManager.login(account, client, source, IPUtils.ipToLong(ip));
        response.setHeader(CommonCode.TOKEN_KEY, token);
        LoginDto dto = memberCenterManager.region(account, ip);
        return JsonWrapper.successWrapper(dto, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("注册会员信息")

    @RequestMapping(value = "register/accountAndMobile", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "registerAccountMobileVo", value = "json格式", paramType = "body", dataType = "registerAccountMobileVo", dataTypeClass = RegisterAccountMobileVo.class, required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "注册成功,返回账号和密码"),
            @ApiResponse(code = 400, message = "请求语法错误"),
            @ApiResponse(code = 500, message = "服务错误"),
            @ApiResponse(code = 405, message = "验证码已过期"),
            @ApiResponse(code = 403, message = "用户被锁定"),
            @ApiResponse(code = 407, message = "域名访问限制"),
            @ApiResponse(code = 430, message = "该手机号已被使用"),
            @ApiResponse(code = 411, message = "用户名或密码格式不对")})

    @Auth(authentication = false, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper register(HttpServletResponse response, HttpServletRequest request, @RequestBody RegisterAccountMobileVo registerAccountMobileVo) {

        Assert.notNull(registerAccountMobileVo, "Object cannot be empty");
        String account = registerAccountMobileVo.getAccount();
        String password = registerAccountMobileVo.getPassword();
        String confirmPassword = registerAccountMobileVo.getConfirmPassword();
        String mobile = registerAccountMobileVo.getMobile();
        String code = registerAccountMobileVo.getCode();
        String inviteCode = registerAccountMobileVo.getInviteCode();
        String iCode = registerAccountMobileVo.getICode();
        Integer source = GlobalCode.getSource(request);
        String ip = IPUtils.getIpAddr(request);
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(code)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        if (!confirmPassword.equals(password)) {
            return JsonWrapper.failureWrapper(ResultCode._416.getCode(), ResultCode._416.getMessage());
        }
        if (!RegxUtils.checkAccount(account)) {
            return JsonWrapper.failureWrapper(ResultCode._300.getCode(), ResultCode._300.getMessage());
        }
        if (!RegxUtils.checkPassword(password)) {
            return JsonWrapper.failureWrapper(ResultCode._301.getCode(), ResultCode._301.getMessage());
        }
        boolean success = memberCenterManager.checkInviteCode(inviteCode);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._433.getCode(), ResultCode._433.getMessage());
        }
        success = memberCenterManager.checkAccount(account);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._409.getCode(), ResultCode._409.getMessage());
        }
        success = memberCenterManager.checkBindMobile(account, mobile);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._430.getCode(), ResultCode._430.getMessage());
        }
        success = memberCenterManager.checkSmsCode(mobile, code);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._405.getCode(), ResultCode._405.getMessage());
        }
        memberCenterManager.register(account, password, mobile, inviteCode, iCode, source, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("会员忘记密码,验证")

    @RequestMapping(value = "password/verify", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "code", value = "验证码", paramType = "query", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 405, message = "验证码已过期"),
            @ApiResponse(code = 418, message = "请求失败,未绑定手机号"),
            @ApiResponse(code = 411, message = "用户名或密码格式不对")
    })
    @Auth(authentication = false, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper forgetVerify(HttpServletResponse response, HttpServletRequest request, String mobile, String code) {

        if (StringUtils.isEmpty(code) || StringUtils.isEmpty(mobile)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        boolean success = memberCenterManager.checkSmsCode(mobile, code);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._405.getCode(), ResultCode._405.getMessage());
        }
        success = memberCenterManager.checkForgetVerify(mobile);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._418.getCode(), ResultCode._418.getMessage());
        }
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("会员忘记登录密码")

    @RequestMapping(value = "password/forget", produces = "application/json; charset=UTF-8", method = {RequestMethod.PUT})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "用户账号", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "password", value = "新密码", paramType = "query", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 411, message = "用户名或密码格式不对")
    })

    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper forgetpassword(HttpServletResponse response, HttpServletRequest request, String account, String password, String confirm) {

        if (!RegxUtils.checkPassword(password)) {
            return JsonWrapper.failureWrapper(ResultCode._301.getCode(), ResultCode._301.getMessage());
        }
        if (!password.equals(confirm)) {
            return JsonWrapper.failureWrapper(ResultCode._416.getCode(), ResultCode._416.getMessage());
        }
        boolean success = memberCenterManager.checkAccount(account);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._418.getCode(), ResultCode._418.getMessage());
        }
        success = memberCenterManager.checkForgetMobile(account);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._454.getCode(), ResultCode._454.getMessage());
        }
        String ip = IPUtils.getIpAddr(request);
        memberCenterManager.updateLoginPwd(account, password, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("修改会员登录密码")

    @RequestMapping(value = "password/logins", produces = "application/json; charset=UTF-8", method = {RequestMethod.PUT})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "passwordVo", value = "修改密码对象", paramType = "body", dataType = "passwordVo", dataTypeClass = PasswordVo.class, required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 301, message = "请求失败，密码格式错误，密码,只能输入6-16位包含大小写字母、数字或字符"),
            @ApiResponse(code = 418, message = "请求失败，未绑定数据"),
            @ApiResponse(code = 405, message = "请求失败，验证码错误")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper updateLoginPwd(HttpServletResponse response, HttpServletRequest request, @RequestBody PasswordVo passwordVo) {

        Assert.notNull(passwordVo, "Object cannot be empty");
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        String mobile = passwordVo.getMobile();
        String password = passwordVo.getPassword();
        String verifyCode = passwordVo.getVerifyCode();
        if (!RegxUtils.checkPassword(password)) {
            return JsonWrapper.failureWrapper(ResultCode._301.getCode(), ResultCode._301.getMessage());
        }
        boolean success = memberCenterManager.checkSmsCode(mobile, verifyCode);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._405.getCode(), ResultCode._405.getMessage());
        }
        success = memberCenterManager.checkBindMobile(account, mobile);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._417.getCode(), ResultCode._417.getMessage());
        }
        String ip = IPUtils.getIpAddr(request);
        memberCenterManager.updateLoginPwd(account, password, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
