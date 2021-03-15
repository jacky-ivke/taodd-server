package com.esports.order.web;

import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.constant.CommonCode;
import com.esports.order.service.DepositOrderManager;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.PageData;
import io.swagger.annotations.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = {"【会员管理系统-订单服务相关接口】"})
@RestController
public class DepositOrderAction {

    @Autowired
    private DepositOrderManager depositOrderManager;

    @ApiOperation("获取存会员款记录")

    @RequestMapping(value = "orders/deposit", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "okstatus", value = "订单状态", paramType = "query", dataType = "int", required = false),
            @ApiImplicitParam(name = "page", value = "分页编号【页码从0开始】", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "pageSize", value = "分页大小【默认10】", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "startTime", value = "开始时间（格式: 2020-01-01）", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间(格式： 2020-01-31)", paramType = "query", dataType = "String", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getDepositOrders(HttpServletResponse response, HttpServletRequest request, Integer okstatus, Integer page, Integer pageSize, String startTime, String endTime) {

        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        JSONObject data = depositOrderManager.getDepositRecords(account, okstatus, page, pageSize, startTime, endTime);
        return JsonWrapper.successWrapper(data, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("公司入款支付回调通知接口【外部系统调用】")

    @RequestMapping(value = "orders/depositCallbak",produces = "application/json; charset=UTF-8", method = {RequestMethod.POST })

    @Auth(authentication = false, domain = false, rateLimiter = @DynamicRateLimiter(enabled = false))
    public String callbackBankAuto(HttpServletResponse response, HttpServletRequest request) {


        return "OK";
    }
}
