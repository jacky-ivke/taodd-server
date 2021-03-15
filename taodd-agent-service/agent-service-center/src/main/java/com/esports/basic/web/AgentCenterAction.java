package com.esports.basic.web;

import com.esports.agent.web.vo.AgentDrawCenterVo;
import com.esports.agent.web.vo.AgentDrawVo;
import com.esports.annotation.Auth;
import com.esports.annotation.DistributedLock;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.basic.dto.AgentProfileDto;
import com.esports.basic.dto.ExtenstionLinksDto;
import com.esports.basic.dto.FinancialCenterDto;
import com.esports.basic.service.AgentCenterManager;
import com.esports.basic.web.vo.AgentPasswordVo;
import com.esports.constant.CommonCode;
import com.esports.constant.GlobalCode;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.IPUtils;
import com.esports.utils.PageData;
import com.esports.utils.RegxUtils;
import io.jsonwebtoken.lang.Assert;
import io.swagger.annotations.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Api(tags = {"【代理管理系统-代理中心相关接口】"})
@RestController
public class AgentCenterAction {

    @Autowired
    private AgentCenterManager agentCenterManager;

    @ApiOperation("获取代理系统交易账单类型")

    @RequestMapping(value = "/agent/bill/type", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "walletType", value = "钱包类型(commission-佣金钱包, other-代存钱包)", paramType = "query", dataType = "String", required = false),
    })

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getBillType(HttpServletResponse response, HttpServletRequest request, String walletType) {

        List<Map<String, Object>> list = agentCenterManager.getBilltype(walletType);
        return JsonWrapper.successWrapper(list, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取代理下级会员信息")

    @RequestMapping(value = "/agent/submember", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "page", value = "手机号", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "pageSize", value = "验证码", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "account", value = "会员账号", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "startTime", value = "开始时间(2020-01-01)", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间（2020-01-10）", paramType = "query", dataType = "String", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getSubMember(HttpServletResponse response, HttpServletRequest request, Integer page, Integer pageSize, String account, String startTime, String endTime) {

        String leader = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        PageData data = agentCenterManager.getSubMember(leader, account, page, pageSize, startTime, endTime);
        return JsonWrapper.successWrapper(data, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取代理个人资料信息")

    @RequestMapping(value = "/agent/profile", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getAgentProfile(HttpServletResponse response, HttpServletRequest request) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        String ip = IPUtils.getIpAddr(request);
        AgentProfileDto dto = agentCenterManager.getAgentProfile(account, ip);
        return JsonWrapper.successWrapper(dto, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取代理财务中心余额总况")

    @RequestMapping(value = "/agent/commission/balance", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getAgentCommissionBalance(HttpServletResponse response, HttpServletRequest request) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        FinancialCenterDto dto = agentCenterManager.getFinancialCenter(account);
        return JsonWrapper.successWrapper(dto, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取代理推广链接")

    @RequestMapping(value = "/agent/extension/links", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getExtensionLinks(HttpServletResponse response, HttpServletRequest request) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        ExtenstionLinksDto dto = agentCenterManager.getExtensionLinks(account);
        return JsonWrapper.successWrapper(dto, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("代理未登录绑定手机接口")

    @RequestMapping(value = "/agent/mobile", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "用户账号", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "mobile", value = "手机号", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "verifyCode", value = "验证码", paramType = "query", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 411, message = "请求失败，提交参数格式有误"),
            @ApiResponse(code = 405, message = "请求失败，验证码错误或者已过期"),
            @ApiResponse(code = 430, message = "请求失败，手机号已经被使用")
    })

    @Auth(authentication = false, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper updateMobile(HttpServletResponse response, HttpServletRequest request, String account, String mobile, String verifyCode) {

        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(mobile) || StringUtils.isEmpty(verifyCode)) {
            return JsonWrapper.successWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        boolean success = agentCenterManager.checkSmsCode(mobile, verifyCode);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._405.getCode(), ResultCode._405.getMessage());
        }
        success = agentCenterManager.checkBindMobile(mobile);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._430.getCode(), ResultCode._430.getMessage());
        }
        String ip = IPUtils.getIpAddr(request);
        agentCenterManager.updateMobile(account, mobile, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("代理修改绑定手机")

    @RequestMapping(value = "/agent/mobile", produces = "application/json; charset=UTF-8", method = {RequestMethod.PUT})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "mobile", value = "绑定手机", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "code", value = "验证码", paramType = "query", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 411, message = "请求失败，提交参数格式有误"),
            @ApiResponse(code = 405, message = "请求失败，验证码错误"),
            @ApiResponse(code = 430, message = "请求失败，手机号已经被使用")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper updateMobile(HttpServletResponse response, HttpServletRequest request, String mobile, String code) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        if (StringUtils.isEmpty(code) || StringUtils.isEmpty(mobile)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        boolean success = agentCenterManager.checkSmsCode(mobile, code);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._405.getCode(), ResultCode._405.getMessage());
        }
        success = agentCenterManager.checkBindMobile(mobile);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._430.getCode(), ResultCode._430.getMessage());
        }
        String ip = IPUtils.getIpAddr(request);
        agentCenterManager.updateMobile(account, mobile, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("代理实名认证")

    @RequestMapping(value = "/agent/realname", produces = "application/json; charset=UTF-8", method = {RequestMethod.PUT})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "realName", value = "实名认证", paramType = "query", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper updateRealName(HttpServletResponse response, HttpServletRequest request, String realName) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        String ip = IPUtils.getIpAddr(request);
        agentCenterManager.updateRealName(account, realName, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("代理绑定邮箱")

    @RequestMapping(value = "/agent/email", produces = "application/json; charset=UTF-8", method = {RequestMethod.PUT})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "email", value = "邮箱号（123456@qq.com）", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "verifyCode", value = "验证码", paramType = "query", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper updateEmail(HttpServletResponse response, HttpServletRequest request, String email, String verifyCode) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(verifyCode)) {
            return JsonWrapper.successWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        boolean success = agentCenterManager.checkEmail(account, email);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._440.getCode(), ResultCode._440.getMessage());
        }
        success = agentCenterManager.checkEmailCode(email, verifyCode);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._405.getCode(), ResultCode._405.getMessage());
        }
        String ip = IPUtils.getIpAddr(request);
        agentCenterManager.updateEmail(account, email, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("代理修改交易密码")

    @RequestMapping(value = "/agent/password/trades", produces = "application/json; charset=UTF-8", method = {RequestMethod.PUT})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "passwordVo", value = "修改密码对象", paramType = "body", dataType = "agentPasswordVo", dataTypeClass = AgentPasswordVo.class, required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 411, message = "请求失败, 提交参数格式有误"),
            @ApiResponse(code = 416, message = "请求失败, 两次提交密码不一致"),
            @ApiResponse(code = 437, message = "请求失败, 交易密码错误")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper updateTradePwd(HttpServletResponse response, HttpServletRequest request, @RequestBody AgentPasswordVo passwordVo) {

        Assert.notNull(passwordVo, "Object cannot be empty");
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        String originalPwd = passwordVo.getOriginalPwd();
        String password = passwordVo.getPassword();
        String repeatPwd = passwordVo.getRepeatPwd();
        if (!RegxUtils.checkPassword(password) || !RegxUtils.checkPassword(originalPwd)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        if (!password.equals(repeatPwd)) {
            return JsonWrapper.failureWrapper(ResultCode._416.getCode(), ResultCode._416.getMessage());
        }
        boolean success = agentCenterManager.checkTradePwd(account, originalPwd);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._447.getCode(), ResultCode._447.getMessage());
        }
        String ip = IPUtils.getIpAddr(request);
        agentCenterManager.updateTradePwd(account, password, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("代理重置支付密码,校验验证码")

    @RequestMapping(value = "/agent/trades/check", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "mobile", value = "手机号", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "verifyCode", value = "验证码", paramType = "query", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 411, message = "请求失败, 提交参数格式有误"),
            @ApiResponse(code = 405, message = "请求失败, 验证码错误或者已过期")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper resetVerify(HttpServletResponse response, HttpServletRequest request, String mobile, String verifyCode) {

        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(verifyCode)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        boolean success = agentCenterManager.checkSmsCode(mobile, verifyCode);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._405.getCode(), ResultCode._405.getMessage());
        }
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("重置支付密码")

    @RequestMapping(value = "/agent/password/trades", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "password", value = "新密码", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "repeatPwd", value = "确认密码", paramType = "query", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 411, message = "请求失败, 提交参数格式有误"),
            @ApiResponse(code = 416, message = "请求失败, 两次提交密码不一致")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper resetTradePwd(HttpServletResponse response, HttpServletRequest request, String password, String repeatPwd) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        if (!RegxUtils.checkPassword(password) || !RegxUtils.checkPassword(repeatPwd)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        if (!password.equals(repeatPwd)) {
            return JsonWrapper.failureWrapper(ResultCode._416.getCode(), ResultCode._416.getMessage());
        }
        String ip = IPUtils.getIpAddr(request);
        agentCenterManager.updateTradePwd(account, password, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("代理取款到银行卡")

    @RequestMapping(value = "/agent/orders/drawtocard", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "agentDrawVo", value = "取款信息", paramType = "body", dataType = "agentDrawVo", dataTypeClass = AgentDrawVo.class, required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 411, message = "请求失败, 提交参数格式有误"),
            @ApiResponse(code = 414, message = "请求失败, 本次提现额度不符合提现规则"),
            @ApiResponse(code = 412, message = "请求失败, 用户余额不足"),
            @ApiResponse(code = 413, message = "请求失败, 未绑定银行卡")
    })

    @Auth(authentication = true, domain = true, sync = @DistributedLock(enabled = true), rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper withdrawalToBank(HttpServletResponse response, HttpServletRequest request, @RequestBody AgentDrawVo agentDrawVo) {

        Assert.notNull(agentDrawVo, "Object cannot be empty");
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        String bankRealName = agentDrawVo.getBankRealName();
        String bankName = agentDrawVo.getBankName();
        String bankAccount = agentDrawVo.getBankAccount();
        BigDecimal amount = agentDrawVo.getAmount();
        Integer source = GlobalCode.getSource(request);
        if (!RegxUtils.checkAmount(amount)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        boolean success = agentCenterManager.checkCommissionScheme(account, amount);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._414.getCode(), ResultCode._414.getMessage());
        }
        success = agentCenterManager.checkAllowDraw(account);
        if(!success){
            return JsonWrapper.failureWrapper(ResultCode._406.getCode(), ResultCode._406.getMessage());
        }
        success = agentCenterManager.checkCommissionBalance(account, amount);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._412.getCode(), ResultCode._412.getMessage());
        }
        success = agentCenterManager.checkBankCard(account);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._413.getCode(), ResultCode._413.getMessage());
        }
        String ip = IPUtils.getIpAddr(request);
        String orderNo = agentCenterManager.withdrawalToBank(source, account, amount, bankRealName, bankName, bankAccount, ip);
        return JsonWrapper.successWrapper(orderNo, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("代理取款到中心钱包")

    @RequestMapping(value = "/agent/orders/drawtobalance", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "agentDrawCenterVo", value = "取款信息", paramType = "body", dataType = "agentDrawCenterVo", dataTypeClass = AgentDrawCenterVo.class, required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 411, message = "请求失败, 提交参数格式有误"),
            @ApiResponse(code = 414, message = "请求失败, 本次提现额度不符合提现规则"),
            @ApiResponse(code = 412, message = "请求失败, 用户余额不足"),
            @ApiResponse(code = 437, message = "请求失败, 交易密码错误")
    })

    @Auth(authentication = true, domain = true, userlocked = true, sync = @DistributedLock(enabled = true), rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper withdrawalToBalance(HttpServletResponse response, HttpServletRequest request, @RequestBody AgentDrawCenterVo agentDrawCenterVo) {

        Assert.notNull(agentDrawCenterVo, "Object cannot be empty");
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        String remarks = agentDrawCenterVo.getRemarks();
        String password = agentDrawCenterVo.getPassword();
        BigDecimal amount = agentDrawCenterVo.getAmount();
        Integer source = GlobalCode.getSource(request);
        if (!RegxUtils.checkAmount(amount) || StringUtils.isEmpty(password)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        boolean success = agentCenterManager.checkCommissionBalance(account, amount);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._412.getCode(), ResultCode._435.getMessage());
        }
        success = agentCenterManager.checkTradePwd(account, password);
        if (!success) {
            return JsonWrapper.successWrapper(ResultCode._437.getCode(), ResultCode._437.getMessage());
        }
        String ip = IPUtils.getIpAddr(request);
        String orderNo = agentCenterManager.withdrawalToBalance(source, account, amount, remarks, ip);
        return JsonWrapper.successWrapper(orderNo, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("创建存款订单")

    @RequestMapping(value = "/agent/orders/deposit", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "channelId", value = "渠道ID", paramType = "query", dataType = "int", required = true),
            @ApiImplicitParam(name = "realName", value = "汇款人", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "amount", value = "存款金额", paramType = "query", dataType = "int", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, identity = "agent", sync = @DistributedLock(enabled = true), rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper createDepositOrder(HttpServletResponse response, HttpServletRequest request, Long channelId, BigDecimal amount, String realName) {

        if (!RegxUtils.checkAmount(amount) || null == channelId) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        Integer source = GlobalCode.getSource(request);
        String ip = IPUtils.getIpAddr(request);
        JSONObject json = agentCenterManager.createAgentDepositOrder(source, account, realName, amount, channelId, ip);
        return JsonWrapper.successWrapper(json, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
