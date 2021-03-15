package com.esports.center.basic.web;

import com.esports.annotation.Auth;
import com.esports.annotation.DistributedLock;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.center.basic.dto.DrawScopeDto;
import com.esports.center.basic.dto.MemberProfileDto;
import com.esports.center.basic.service.MemberCenterManager;
import com.esports.center.basic.web.vo.DrawDto;
import com.esports.center.basic.web.vo.MemberPasswordVo;
import com.esports.constant.CommonCode;
import com.esports.constant.GlobalCode;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.IPUtils;
import com.esports.utils.RegxUtils;
import io.swagger.annotations.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
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

@Api(tags = {"【会员管理系统-会员中心相关接口】"})
@RestController
public class MemberCenterAction {

    @Autowired
    private MemberCenterManager memberCenterManager;


    @ApiOperation("获取会员取款限制条件")

    @RequestMapping(value = "draw/rules", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getDrawRules(HttpServletResponse response, HttpServletRequest request) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        DrawScopeDto dto = memberCenterManager.getDrawRules(account);
        return JsonWrapper.successWrapper(dto, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取游戏类型")

    @RequestMapping(value = "games/type", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })
    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getGameTypes(HttpServletResponse response, HttpServletRequest request) {

        List<Map<String, Object>> list = memberCenterManager.getGameTypes();
        return JsonWrapper.successWrapper(list, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取会员交易账单类型")

    @RequestMapping(value = "bill/type", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)
    })

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getBillType(HttpServletResponse response, HttpServletRequest request) {

        List<Map<String, Object>> list = memberCenterManager.getTradeBillType();
        return JsonWrapper.successWrapper(list, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取会员中心账户余额")

    @RequestMapping(value = "balance", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getMemberBalance(HttpServletResponse response, HttpServletRequest request) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        BigDecimal balance = memberCenterManager.getMemberBalance(account);
        return JsonWrapper.successWrapper(balance, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取利息钱包余额")

    @RequestMapping(value = "interest/balance", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getInterestBalance(HttpServletResponse response, HttpServletRequest request) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        BigDecimal interestBalance = memberCenterManager.getInterestBalance(account);
        return JsonWrapper.successWrapper(interestBalance, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("检查会员是否是具有代理权限")

    @RequestMapping(value = "/agent/identity/verify", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 418, message = "请求失败, 未绑定手机号"),
            @ApiResponse(code = 444, message = "请求失败, 代理资料审核中,请不要重复操作")
    })

    @Auth(authentication = true, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper ccheckIdentity(HttpServletResponse response, HttpServletRequest request) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        boolean success = memberCenterManager.checkIdentity(account);
        return JsonWrapper.successWrapper(success, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("会员申请加入合营计划")

    @RequestMapping(value = "/agent/join/plan", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 418, message = "请求失败, 未绑定手机号"),
            @ApiResponse(code = 444, message = "请求失败, 代理资料审核中,请不要重复操作")
    })

    @Auth(authentication = true, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper saveJoinPlan(HttpServletResponse response, HttpServletRequest request, Integer source) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        String ip = IPUtils.getIpAddr(request);
        boolean success = memberCenterManager.checkHasMobile(account);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._418.getCode(), ResultCode._418.getMessage());
        }
        success = memberCenterManager.checkAgentAuditStatus(account);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._444.getCode(), ResultCode._444.getMessage());
        }
        memberCenterManager.saveJoinPlan(account, source, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取会员个人资料信息")

    @RequestMapping(value = "profile", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getProfile(HttpServletResponse response, HttpServletRequest request) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        String ip = IPUtils.getIpAddr(request);
        MemberProfileDto dto = memberCenterManager.getProfile(account, ip);
        return JsonWrapper.successWrapper(dto, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("提交会员个人资料信息")

    @RequestMapping(value = "profile", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "memberProfileDto", value = "个人资料", paramType = "body", dataType = "memberProfileDto", dataTypeClass = MemberProfileDto.class, required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 438, message = "请求失败，QQ号已被使用"),
            @ApiResponse(code = 439, message = "请求失败，微信号已被使用"),
            @ApiResponse(code = 455, message = "请求失败，不允许多次变更个人资料信息"),
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper saveProfile(HttpServletResponse response, HttpServletRequest request, @RequestBody MemberProfileDto memberProfileDto) {

        Assert.notNull(memberProfileDto, "Object cannot be empty");
        String qq = memberProfileDto.getQq();
        String wechat = memberProfileDto.getWechat();
        String realName = memberProfileDto.getRealName();
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));

        boolean success = memberCenterManager.checkProfile(account, qq, wechat, realName);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._455.getCode(), ResultCode._455.getMessage());
        }
        success = memberCenterManager.checkQQ(account, qq);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._438.getCode(), ResultCode._438.getMessage());
        }
        success = memberCenterManager.checkWechat(account, wechat);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._439.getCode(), ResultCode._439.getMessage());
        }
        String ip = IPUtils.getIpAddr(request);
        memberCenterManager.saveProfile(account, memberProfileDto, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("会员修改绑定手机")

    @RequestMapping(value = "mobile", produces = "application/json; charset=UTF-8", method = {RequestMethod.PUT})

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
        boolean success = memberCenterManager.checkSmsCode(mobile, code);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._405.getCode(), ResultCode._405.getMessage());
        }
        success = memberCenterManager.checkBindMobile(account, mobile);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._430.getCode(), ResultCode._430.getMessage());
        }
        String ip = IPUtils.getIpAddr(request);
        memberCenterManager.updateMobile(account, mobile, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("会员修改真实姓名")

    @RequestMapping(value = "realname", produces = "application/json; charset=UTF-8", method = {RequestMethod.PUT})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "realName", value = "真实姓名", paramType = "query", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 411, message = "请求失败，提交参数格式有误")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper updateRealName(HttpServletResponse response, HttpServletRequest request, String realName) {

        if (StringUtils.isEmpty(realName)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        String ip = IPUtils.getIpAddr(request);
        memberCenterManager.updateRealName(account, realName, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("会员绑定邮箱")

    @RequestMapping(value = "email", produces = "application/json; charset=UTF-8", method = {RequestMethod.PUT})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "email", value = "邮箱", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "verifyCode", value = "验证码", paramType = "query", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 440, message = "请求失败，邮箱已被绑定"),
            @ApiResponse(code = 405, message = "请求失败，验证码错误或者已过期")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper updateEmail(HttpServletResponse response, HttpServletRequest request, String email, String verifyCode) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(verifyCode)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        boolean success = memberCenterManager.checkEmail(account, email);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._440.getCode(), ResultCode._440.getMessage());
        }
        success = memberCenterManager.checkEmailCode(email, verifyCode);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._405.getCode(), ResultCode._405.getMessage());
        }
        String ip = IPUtils.getIpAddr(request);
        memberCenterManager.updateEmail(account, email, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("创建存款订单")

    @RequestMapping(value = "orders/deposit", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "channelId", value = "渠道ID", paramType = "query", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "source", value = "来源【0、PC 1、H5】", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "amount", value = "存款金额", paramType = "query", dataType = "Integer", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 411, message = "请求失败, 请求参数不合法"),
            @ApiResponse(code = 434, message = "请求失败，充值金额不符合渠道设置标准")
    })

    @Auth(authentication = true, domain = true, userlocked = true, sync = @DistributedLock(enabled = true), rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper createOrder(HttpServletResponse response, HttpServletRequest request, Long channelId, BigDecimal amount) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        if (!RegxUtils.checkAmount(amount) || null == channelId) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        boolean success = memberCenterManager.checkDepositRule(channelId, amount);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._434.getCode(), ResultCode._434.getMessage());
        }
        success = memberCenterManager.checkDepositTotalNum(account);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._452.getCode(), ResultCode._452.getMessage());
        }
        success = memberCenterManager.checkDepositFailureRate(account);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._453.getCode(), ResultCode._453.getMessage());
        }
        Integer source = GlobalCode.getSource(request);
        String ip = IPUtils.getIpAddr(request);
        JSONObject json = memberCenterManager.createMemberDepositOrder(source, account, amount, channelId, ip);
        return JsonWrapper.successWrapper(json, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("创建取款订单")

    @RequestMapping(value = "orders/draw", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "drawDto", value = "取款信息", paramType = "body", dataType = "drawDto", dataTypeClass = DrawDto.class, required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 406, message = "请求失败, 提现渠道维护中"),
            @ApiResponse(code = 411, message = "请求失败, 请求参数不合法"),
            @ApiResponse(code = 413, message = "请求失败，未绑定银行卡"),
            @ApiResponse(code = 414, message = "请求失败，本次提现额度不符合提现规则"),
            @ApiResponse(code = 415, message = "请求失败，本次提现已超额或今日提现次数已用完"),
            @ApiResponse(code = 412, message = "请求失败，用户余额不足")
    })

    @Auth(authentication = true, domain = true, userlocked = true, sync = @DistributedLock(enabled = true), rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper createOrder(HttpServletResponse response, HttpServletRequest request, @RequestBody DrawDto drawDto) {

        Assert.notNull(drawDto, "Object cannot be empty");
        String bankRealName = drawDto.getBankRealName();
        String bankName = drawDto.getBankName();
        String bankAccount = drawDto.getBankAccount();
        String password = drawDto.getPassword();
        BigDecimal amount = drawDto.getAmount();
        Integer source = GlobalCode.getSource(request);
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        String ip = IPUtils.getIpAddr(request);
        Integer lang = 1;
        if (!RegxUtils.checkAmount(amount) || StringUtils.isEmpty(bankAccount) || StringUtils.isEmpty(password)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        String drawSchemeCode = memberCenterManager.getDrawScheme(account);
        Integer vip = memberCenterManager.getVip(account);
        boolean success = memberCenterManager.checkAllowDraw(account, drawSchemeCode, vip);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._406.getCode(), ResultCode._406.getMessage());
        }
        success = memberCenterManager.checkTradePwd(account, password);
        if (!success) {
            return JsonWrapper.successWrapper(ResultCode._437.getCode(), ResultCode._437.getMessage());
        }
        success = memberCenterManager.checkBalance(account, amount);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._412.getCode(), ResultCode._412.getMessage());
        }
        success = memberCenterManager.checkBankCard(account);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._413.getCode(), ResultCode._413.getMessage());
        }
        success = memberCenterManager.checkDrawScope(amount, drawSchemeCode, vip);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._414.getCode(), ResultCode._414.getMessage());
        }
        success = memberCenterManager.checkDrawTotalOrDrawCount(account, drawSchemeCode, vip);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._415.getCode(), ResultCode._415.getMessage());
        }
        String orderNo = memberCenterManager.createMemberDrawOrder(drawSchemeCode, vip, source, account, amount, bankRealName, bankName, bankAccount, ip);
        return JsonWrapper.successWrapper(orderNo, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("会员修改交易密码")

    @RequestMapping(value = "/password/trades", produces = "application/json; charset=UTF-8", method = {RequestMethod.PUT})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "passwordVo", value = "修改密码对象", paramType = "body", dataType = "memberPasswordVo", dataTypeClass = MemberPasswordVo.class, required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 411, message = "请求失败, 提交参数格式有误"),
            @ApiResponse(code = 416, message = "请求失败, 两次提交密码不一致"),
            @ApiResponse(code = 437, message = "请求失败, 交易密码错误")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper updateTradePwd(HttpServletResponse response, HttpServletRequest request, @RequestBody MemberPasswordVo passwordVo) {

        Assert.notNull(passwordVo, "Object cannot be empty");
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        String originalPwd = passwordVo.getOriginalPwd();
        String password = passwordVo.getPassword();
        String repeatPwd = passwordVo.getRepeatPwd();
        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(repeatPwd)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        if(!RegxUtils.checkPassword(password)){
            return JsonWrapper.failureWrapper(ResultCode._416.getCode(), ResultCode._416.getMessage());
        }
        if (!password.equals(repeatPwd)) {
            return JsonWrapper.failureWrapper(ResultCode._301.getCode(), ResultCode._301.getMessage());
        }
        boolean success = memberCenterManager.checkTradePwd(account, originalPwd);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._447.getCode(), ResultCode._447.getMessage());
        }
        String ip = IPUtils.getIpAddr(request);
        memberCenterManager.updateTradePwd(account, password, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("会员重置支付密码,校验验证码")

    @RequestMapping(value = "/trades/check", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

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
        boolean success = memberCenterManager.checkSmsCode(mobile, verifyCode);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._405.getCode(), ResultCode._405.getMessage());
        }


        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("会员重置支付密码")

    @RequestMapping(value = "/password/trades", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

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
        if (!RegxUtils.checkPassword(password)) {
            return JsonWrapper.failureWrapper(ResultCode._301.getCode(), ResultCode._301.getMessage());
        }
        if (!password.equals(repeatPwd)) {
            return JsonWrapper.failureWrapper(ResultCode._416.getCode(), ResultCode._416.getMessage());
        }
        String ip = IPUtils.getIpAddr(request);
        memberCenterManager.updateTradePwd(account, password, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
