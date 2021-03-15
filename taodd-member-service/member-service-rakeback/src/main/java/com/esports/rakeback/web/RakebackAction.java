package com.esports.rakeback.web;

import com.esports.annotation.Auth;
import com.esports.annotation.DistributedLock;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.constant.CommonCode;
import com.esports.rakeback.service.RakebackManager;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.IPUtils;
import com.esports.utils.PageData;
import io.swagger.annotations.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

@Api(tags = {"【会员管理系统-洗码反水相关接口】"})
@RestController
public class RakebackAction {

    @Autowired
    private RakebackManager rakebackManager;


    @ApiOperation("获取会员洗码反水记录")

    @RequestMapping(value = "orders/wash", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "apiCode", value = "编号", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "gameType", value = "游戏类型", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "page", value = "分页编号【页码从0开始】", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "pageSize", value = "分页大小【默认10】", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "startTime", value = "开始时间（2020-01-01）", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间(2020-01-10)", paramType = "query", dataType = "String", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getWashOrders(HttpServletResponse response, HttpServletRequest request, Integer page, String apiCode, String gameType, Integer pageSize, String startTime, String endTime) {

        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        JSONObject data = rakebackManager.getWashRecords(account, page, pageSize, apiCode, gameType, startTime, endTime);
        return JsonWrapper.successWrapper(data, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取会员洗码反水明细")

    @RequestMapping(value = "washing/detail", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "gameType", value = "游戏类型", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "page", value = "分页编号【页码从0开始】", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "pageSize", value = "分页大小【默认10】", paramType = "query", dataType = "Integer", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getWashDetail(HttpServletResponse response, HttpServletRequest request, Integer page, Integer pageSize, String gameType) {

        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        PageData pageData = rakebackManager.getWashDetail(account, gameType, page, pageSize);
        return JsonWrapper.successWrapper(pageData, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取会员本期预计洗码反水总额")

    @RequestMapping(value = "betting/washing", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getRakebackTotalAmount(HttpServletResponse response, HttpServletRequest request) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        BigDecimal rakeAmount = rakebackManager.getWashTotalAmount(account);
        return JsonWrapper.successWrapper(rakeAmount, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("一键结算本期洗码")

    @RequestMapping(value = "betting/onekey", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 450, message = "洗码金额不能低于1元"),
    })

    @Auth(authentication = true, domain = true, userlocked = true, sync = @DistributedLock(enabled = true), rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper oneKeyWashCode(HttpServletResponse response, HttpServletRequest request) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        String ip = IPUtils.getIpAddr(request);
        boolean result = rakebackManager.checkWashAmount(account);
        if (!result) {
            return JsonWrapper.failureWrapper(ResultCode._450.getCode(), ResultCode._450.getMessage());
        }
        BigDecimal rakeAmount = rakebackManager.oneKeyWashCode(account, ip);
        return JsonWrapper.successWrapper(rakeAmount, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
