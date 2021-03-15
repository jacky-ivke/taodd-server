package com.esports.order.web;


import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.constant.CommonCode;
import com.esports.order.service.ActivityOrderManager;
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
public class ActivityOrderAction {

    @Autowired
    private ActivityOrderManager activityOrderManager;

    @ApiOperation("获取礼金记录")

    @RequestMapping(value = "orders/activity", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "okstatus", value = "订单状态", paramType = "query", dataType = "int", required = false),
            @ApiImplicitParam(name = "type", value = "活动类型", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "page", value = "分页编号【页码从0开始】", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "pageSize", value = "分页大小【默认10】", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "startTime", value = "开始时间(2020-01-01)", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间（2020-01-10）", paramType = "query", dataType = "String", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getOrders(HttpServletResponse response, HttpServletRequest request, Integer page, Integer pageSize, Integer okstatus, String type, String startTime, String endTime) {

        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        JSONObject data = activityOrderManager.getActvitRecords(account, page, pageSize, okstatus, type, startTime, endTime);
        return JsonWrapper.successWrapper(data, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
