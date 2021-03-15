package com.esports.bankcard.web;

import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.bankcard.bean.db1.AgentBankCard;
import com.esports.bankcard.dto.BankCardDto;
import com.esports.bankcard.service.AgentBankCardManager;
import com.esports.bankcard.web.vo.BankCardVo;
import com.esports.constant.CommonCode;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.IPUtils;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = {"【代理管理系统-代理中心相关接口】"})
@RestController
public class AgentBankCardAction {

    @Autowired
    private AgentBankCardManager bankCardManager;

    @ApiOperation("代理提交绑定银行卡信息")

    @RequestMapping(value = "/agent/bankcards", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "bankCardVo", value = "绑定信息", paramType = "body", dataType = "bankCardVo", dataTypeClass = BankCardVo.class, required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 410, message = "请求失败, 银行卡数量超过限制3张"),
            @ApiResponse(code = 410, message = "请求失败, 银行卡已经存在")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper bindBankcard(HttpServletResponse response, HttpServletRequest request, @RequestBody BankCardVo bankCardVo) {

        Assert.notNull(bankCardVo, "Object cannot be empty");
        String account = request.getAttribute(CommonCode.JWT_USERNAME).toString();
        boolean success = bankCardManager.checkCardNum(account);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._446.getCode(), ResultCode._446.getMessage());
        }
        success = bankCardManager.checkCardNo(bankCardVo.getBankAccount());
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._410.getCode(), ResultCode._410.getMessage());
        }
        AgentBankCard bankcard = new AgentBankCard();
        BeanUtils.copyProperties(bankCardVo, bankcard);
        String ip = IPUtils.getIpAddr(request);
        bankCardManager.bindBankcard(account, bankcard, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取代理绑定的银行卡")

    @RequestMapping(value = "/agent/bankcards", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = false, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getBankcards(HttpServletResponse response, HttpServletRequest request) {

        String account = request.getAttribute(CommonCode.JWT_USERNAME).toString();
        List<BankCardDto> list = bankCardManager.getBankcards(account);
        return JsonWrapper.successWrapper(list, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
