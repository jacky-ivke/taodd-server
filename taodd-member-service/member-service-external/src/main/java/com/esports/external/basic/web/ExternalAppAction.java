package com.esports.external.basic.web;

import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.annotation.VisitLimiter;
import com.esports.constant.CommonCode;
import com.esports.constant.GlobalCode;
import com.esports.external.basic.service.ExternalAppManager;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.IPUtils;
import com.esports.utils.PageData;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

@Api(tags = {"【会员管理系统-第三方应用相关接口】"})
@RestController
public class ExternalAppAction {

    @Autowired
    private ExternalAppManager externalAppManager;

    @ApiOperation("获取会员游戏平台列表")

    @RequestMapping(value = "apis", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameType", value = "游戏类型", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "page", value = "分页编号【页码从0开始】", paramType = "query", dataType = "int", required = false),
            @ApiImplicitParam(name = "pageSize", value = "分页大小【默认10】", paramType = "query", dataType = "int", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getApiList(HttpServletResponse response, HttpServletRequest request, String gameType, Integer page, Integer pageSize) {

        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        PageData data = externalAppManager.getApiList(gameType, page, pageSize);
        return JsonWrapper.successWrapper(data, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取会员各个游戏平台以及余额")

    @RequestMapping(value = "apis/detail", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getApiDetail(HttpServletResponse response, HttpServletRequest request) {

        String account = request.getAttribute(CommonCode.JWT_USERNAME).toString();
        String ip = IPUtils.getIpAddr(request);
        PageData data = externalAppManager.getApiDetail(account, ip);
        return JsonWrapper.successWrapper(data, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("一键回收会员游戏钱包金额（注意部分平台无法回收小数）")

    @RequestMapping(value = "apis/recovery", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @Auth(authentication = true, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true), visitLimiter = @VisitLimiter(enabled = true, time = 3, visits = 1))
    public JsonWrapper oneKeyRecovery(HttpServletResponse response, HttpServletRequest request) {

        String account = request.getAttribute(CommonCode.JWT_USERNAME).toString();
        String ip = IPUtils.getIpAddr(request);
        Integer deviceType = GlobalCode.getSource(request);
        Integer lang = 1;
        externalAppManager.oneKeyRecovery(account, deviceType, lang, ip);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("查询会员游戏钱包总余额")

    @RequestMapping(value = "apis/totalBalance", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @Auth(authentication = true, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true), visitLimiter = @VisitLimiter(enabled = true, time = 3, visits = 1))
    public JsonWrapper getApiTotalBalance(HttpServletResponse response, HttpServletRequest request) {

        String account = request.getAttribute(CommonCode.JWT_USERNAME).toString();
        String ip = IPUtils.getIpAddr(request);
        BigDecimal totalBalance = externalAppManager.getApiTotalBalance(account,ip);
        return JsonWrapper.successWrapper(totalBalance, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
