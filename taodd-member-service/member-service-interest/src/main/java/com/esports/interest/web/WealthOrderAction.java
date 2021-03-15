package com.esports.interest.web;

import com.alibaba.fastjson.JSONObject;
import com.esports.annotation.Auth;
import com.esports.annotation.DistributedLock;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.constant.CommonCode;
import com.esports.interest.dto.InterestDto;
import com.esports.interest.service.WealthManager;
import com.esports.interest.web.vo.WealthVo;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.IPUtils;
import com.esports.utils.PageData;
import com.esports.utils.RegxUtils;
import io.jsonwebtoken.lang.Assert;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

@Api(tags = {"【会员管理系统-利息钱包相关接口】"})
@RestController
public class WealthOrderAction {

    @Autowired
    private WealthManager wealthManager;

    @ApiOperation("获取会员利息钱包收益总况")

    @RequestMapping(value = "interest/report", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })
    @Auth(authentication = true, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getInterestReport(HttpServletResponse response, HttpServletRequest request) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        InterestDto dto = wealthManager.getInterestReport(account);
        return JsonWrapper.successWrapper(dto, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取会员可转出理财订单")

    @RequestMapping(value = "orders/valid/wealth", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "status", value = "状态", paramType = "query", dataType = "int", required = false),
            @ApiImplicitParam(name = "page", value = "页码", paramType = "query", dataType = "int", required = false),
            @ApiImplicitParam(name = "pageSize", value = "页大小", paramType = "query", dataType = "int", required = false),
            @ApiImplicitParam(name = "startTime", value = "开始时间（格式: 2020-01-01）", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间(格式： 2020-01-31)", paramType = "query", dataType = "String", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getOrders(HttpServletResponse response, HttpServletRequest request, Integer page, Integer pageSize, Integer status, String startTime, String endTime) {

        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        JSONObject data = wealthManager.getValidRecords(account, page, pageSize, status, startTime, endTime);
        return JsonWrapper.successWrapper(data, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取会员利息钱包转入/转出记录")

    @RequestMapping(value = "orders/wealth", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "receive", value = "是否领取", paramType = "query", dataType = "boolean", required = false),
            @ApiImplicitParam(name = "page", value = "页码", paramType = "query", dataType = "int", required = false),
            @ApiImplicitParam(name = "pageSize", value = "页大小", paramType = "query", dataType = "int", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getOrders(HttpServletResponse response, HttpServletRequest request, Boolean receive, Integer page, Integer pageSize) {

        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        PageData data = wealthManager.getOrders(account, receive, page, pageSize);
        return JsonWrapper.successWrapper(data, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("会员转出利息钱包,提前转出不计收益")

    @RequestMapping(value = "orders/wealth", produces = "application/json; charset=UTF-8", method = {RequestMethod.PUT})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "wealthVo", value = "领取理财套餐", paramType = "body", dataType = "wealthVo", dataTypeClass = WealthVo.class, required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, sync = @DistributedLock(enabled = true), rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper updateOrder(HttpServletResponse response, HttpServletRequest request, @RequestBody WealthVo wealthVo) {

        Assert.notNull(wealthVo, "Object cannot be empty");
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        String ip = IPUtils.getIpAddr(request);
        String[] orders = wealthVo.getOrderNo();
        wealthManager.drawOrder(orders, account, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("会员转入利息钱包")

    @RequestMapping(value = "orders/wealth", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "packageId", value = "理财套餐ID", paramType = "query", dataType = "Long", required = true),
            @ApiImplicitParam(name = "amount", value = "理财金额", paramType = "query", dataType = "Integer", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 411, message = "请求失败,请求参数不合法"),
            @ApiResponse(code = 449, message = "请求失败,理财金额不能低于100元"),
            @ApiResponse(code = 412, message = "请求失败,用户余额不足")
    })

    @Auth(authentication = true, domain = true, sync = @DistributedLock(enabled = true), rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper createOrder(HttpServletResponse response, HttpServletRequest request, Long packageId, BigDecimal amount) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        if (!RegxUtils.checkAmount(amount) || null == packageId) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        boolean result = wealthManager.checkMinAmount(amount);
        if (!result) {
            return JsonWrapper.failureWrapper(ResultCode._449.getCode(), ResultCode._449.getMessage());
        }
        result = wealthManager.checkBalance(account, amount);
        if (!result) {
            return JsonWrapper.failureWrapper(ResultCode._412.getCode(), ResultCode._412.getMessage());
        }
        String ip = IPUtils.getIpAddr(request);
        String orderNo = wealthManager.createOrder(account, packageId, amount, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}