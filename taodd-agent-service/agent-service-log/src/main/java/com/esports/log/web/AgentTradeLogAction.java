package com.esports.log.web;

import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.constant.CommonCode;
import com.esports.log.service.AgentTradeLogManager;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.PageData;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = {"【代理管理系统-日志记录相关接口】"})
@RestController
public class AgentTradeLogAction {

    @Autowired
    private AgentTradeLogManager agentTradeLogManager;

    @ApiOperation("获取代理交易日志记录")

    @RequestMapping(value = "/agent/trade", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "walletType", value = "钱包类型(commission-佣金钱包, other-代存钱包)", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "type", value = "账单类型", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "okstatus", value = "订单状态", paramType = "query", dataType = "int", required = false),
            @ApiImplicitParam(name = "page", value = "页码", paramType = "query", dataType = "int", required = false),
            @ApiImplicitParam(name = "pageSize", value = "页大小", paramType = "query", dataType = "int", required = false),
            @ApiImplicitParam(name = "startTime", value = "开始时间(2020-05-01)", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间( 2020-05-31)", paramType = "query", dataType = "String", required = false)
    })

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getOrders(HttpServletResponse response, HttpServletRequest request, String walletType, Integer okstatus, String type, Integer page, Integer pageSize, String startTime, String endTime) {

        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        PageData data = agentTradeLogManager.getOrders(walletType, account, okstatus, type, page, pageSize, startTime, endTime);
        return JsonWrapper.successWrapper(data, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}


