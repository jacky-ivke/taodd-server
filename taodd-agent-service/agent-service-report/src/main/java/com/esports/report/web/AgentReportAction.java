package com.esports.report.web;

import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.constant.CommonCode;
import com.esports.report.dto.*;
import com.esports.report.service.AgentReportManager;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.DateUtils;
import com.esports.utils.PageData;
import io.swagger.annotations.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = {"【代理管理系统-代理报表相关接口】"})
@RestController
public class AgentReportAction {

    @Autowired
    private AgentReportManager agentReportManager;

    @ApiOperation("获取本月数据")

    @RequestMapping(value = "/agent/month/datas", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getCurrentMonthData(HttpServletResponse response, HttpServletRequest request) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        MonthDataDto dto = agentReportManager.getCurrentMonthData(account);
        return JsonWrapper.successWrapper(dto, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取游戏记录")

    @RequestMapping(value = "/agent/game/records", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "page", value = "页码", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "account", value = "会员账号", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "apiCode", value = "平台编号(BBIN、AG、OG等)", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "startTime", value = "开始时间（默认本月第一天 2020-01-01）", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间(默认本月最后一天  2020-01-31)", paramType = "query", dataType = "String", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, identity = "agent", rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getGameRecord(HttpServletResponse response, HttpServletRequest request, String account, String apiCode, Integer page, Integer pageSize, String startTime, String endTime) {

        String leader = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        PageData pageData = agentReportManager.getSubMemberBet(leader, account, page, pageSize, apiCode, startTime, endTime);
        return JsonWrapper.successWrapper(null, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("财务报表")

    @RequestMapping(value = "/agent/finance/report", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间(默认本月第一天  2020-05-01)", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间(默认本月最后一天 2020-05-31)", paramType = "query", dataType = "String", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getFinaceReport(HttpServletResponse response, HttpServletRequest request, String startTime, String endTime) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        FinanceReportDto dto = agentReportManager.getFinanceReport(account, startTime, endTime);
        return JsonWrapper.successWrapper(dto, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("佣金报表")

    @RequestMapping(value = "/agent/commission/report", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "month", value = "月份（默认取当前月份 2020-05）", paramType = "query", dataType = "String", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getCommissionReport(HttpServletResponse response, HttpServletRequest request, String month) {

        month = DateUtils.formatMonth(month);
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        CommissionReportDto dto = agentReportManager.getCommissionReport(account, month);
        return JsonWrapper.successWrapper(dto, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("代理系统，平台报表")

    @RequestMapping(value = "/agent/platform/report", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getPlatformReport(HttpServletResponse response, HttpServletRequest request) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        PlatformDataDto data = agentReportManager.getPlatformReport(account);
        return JsonWrapper.successWrapper(data, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("佣金报表,详情")

    @RequestMapping(value = "/agent/commission/detail", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "month", value = "月份（默认取当前月份 2020-05）", paramType = "query", dataType = "String", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, identity = "agent", rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getCommissionDetail(HttpServletResponse response, HttpServletRequest request, String month) {

        month = DateUtils.formatMonth(month);
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        CommissionDetailDto dto = agentReportManager.getCommissionDetail(account, month);
        return JsonWrapper.successWrapper(dto, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("数据对比,人数")

    @RequestMapping(value = "/agent/compare/active", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "month", value = "比对时间（2020-05 默认取当前月份）", paramType = "query", dataType = "String", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, identity = "agent", rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getDataCompareActvie(HttpServletResponse response, HttpServletRequest request, String month) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        JSONObject json = agentReportManager.getDataCompareActvie(account, month);
        return JsonWrapper.successWrapper(json, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("数据对比, 金额")

    @RequestMapping(value = "/agent/compare/amount", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "month", value = "比对时间（2020-05 默认取当前月份）", paramType = "query", dataType = "String", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, identity = "agent", rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getDataCompareAmount(HttpServletResponse response, HttpServletRequest request, String month) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        JSONObject json = agentReportManager.getDataCompareAmount(account, month);
        return JsonWrapper.successWrapper(json, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取代理返佣方案,佣金比例")
    @RequestMapping(value = "/agent/commission/scheme", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "month", value = "月份（默认取当前月份 2020-05）", paramType = "query", dataType = "String", required = false)})

    @Auth(authentication = true, domain = true, userlocked = true, identity = "agent", rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getCommissionScheme(HttpServletResponse response, HttpServletRequest request, String month) {
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        month = DateUtils.formatMonth(month);
        if (StringUtils.isEmpty(month)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        List<CommissionSchemeDto> dtos = agentReportManager.getCommissionScheme(account, month);
        return JsonWrapper.successWrapper(dtos, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
