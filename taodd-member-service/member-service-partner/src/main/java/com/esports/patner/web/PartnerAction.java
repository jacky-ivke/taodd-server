package com.esports.patner.web;

import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.annotation.VisitLimiter;
import com.esports.constant.CommonCode;
import com.esports.constant.GlobalCode;
import com.esports.patner.dto.PartnerProfileDto;
import com.esports.patner.service.PartnerManager;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.IPUtils;
import com.esports.utils.PageData;
import com.esports.utils.RegxUtils;
import io.swagger.annotations.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = {"【会员管理系统-超级合伙人相关接口】"})
@RestController
public class PartnerAction {

    @Autowired
    private PartnerManager partnerManager;


    @ApiOperation("超级合伙人,添加好友")

    @RequestMapping(value = "partner/register", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "account", value = "合伙人账号", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "password", value = "合伙人密码", paramType = "query", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 300, message = "请求失败，账号格式错误"),
            @ApiResponse(code = 301, message = "请求失败，密码格式错误"),
            @ApiResponse(code = 409, message = "请求失败，注册账号已经存在"),
            @ApiResponse(code = 402, message = "请求失败，账号或者密码错误")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper saveFriends(HttpServletResponse response, HttpServletRequest request, String account, String password) {

        String inviter = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        Integer source = GlobalCode.getSource(request);
        if (!RegxUtils.checkAccount(account)) {
            return JsonWrapper.failureWrapper(ResultCode._300.getCode(), ResultCode._300.getMessage());
        }
        if (!RegxUtils.checkPassword(password)) {
            return JsonWrapper.failureWrapper(ResultCode._301.getCode(), ResultCode._301.getMessage());
        }
        boolean result = partnerManager.checkAccount(account);
        if (!result) {
            return JsonWrapper.failureWrapper(ResultCode._409.getCode(), ResultCode._409.getMessage());
        }
        String ip = IPUtils.getIpAddr(request);
        partnerManager.createAccount(account, password, inviter, source, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取超级合伙人信息")

    @RequestMapping(value = "partner/profile", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true), visitLimiter = @VisitLimiter(enabled = true))
    public JsonWrapper getPartnerProfile(HttpServletResponse response, HttpServletRequest request) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        PartnerProfileDto profileDto = partnerManager.getProfile(account);
        return JsonWrapper.successWrapper(profileDto, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取超级合伙人好友列表")

    @RequestMapping(value = "partner/friends", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "page", value = "分页编号【页码从0开始】", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "pageSize", value = "分页大小【默认10】", paramType = "query", dataType = "Integer", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getFriends(HttpServletResponse response, HttpServletRequest request, Integer page, Integer pageSize) {

        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        PageData data = partnerManager.getFriends(account, page, pageSize);
        return JsonWrapper.successWrapper(data, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取超级合伙人账目统计")

    @RequestMapping(value = "orders/commission", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "page", value = "分页编号【页码从0开始】", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "pageSize", value = "分页大小【默认10】", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "startTime", value = "开始时间(2020-05-01)", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间(2020-05-07)", paramType = "query", dataType = "String", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getOrders(HttpServletResponse response, HttpServletRequest request, Integer page, Integer pageSize, String startTime, String endTime) {

        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        JSONObject data = partnerManager.getPartnerFriendsBetRecords(account, page, pageSize, startTime, endTime);
        return JsonWrapper.successWrapper(data, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取超级合伙人佣金记录")

    @RequestMapping(value = "orders/commission/draw", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "page", value = "分页编号【页码从0开始】", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "pageSize", value = "分页大小【默认10】", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "startTime", value = "开始时间(2020-05-01)", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间(2020-05-07)", paramType = "query", dataType = "String", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getRecommCommissionOrders(HttpServletResponse response, HttpServletRequest request, Integer page, Integer pageSize, String startTime, String endTime) {

        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        JSONObject data = partnerManager.getPartnerCommissionRecords(account, page, pageSize, startTime, endTime);
        return JsonWrapper.successWrapper(data, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}


