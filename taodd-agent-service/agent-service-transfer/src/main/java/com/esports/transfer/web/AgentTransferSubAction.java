package com.esports.transfer.web;

import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.constant.CommonCode;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.transfer.service.AgentTransferSubManager;
import com.esports.utils.IPUtils;
import com.esports.utils.PageData;
import com.esports.utils.RegxUtils;
import com.esports.transfer.web.vo.TransferSubVo;
import io.jsonwebtoken.lang.Assert;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

@Api(tags = {"【代理管理系统-代存相关接口】"})
@RestController
public class AgentTransferSubAction {

    @Autowired
    private AgentTransferSubManager agentTransferSubManager;

    @ApiOperation("确认代存,提交代存信息")

    @RequestMapping(value = "/agent/transfer/submember", produces = "application/json; charset=UTF-8", method = { RequestMethod.POST })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "transferSubVo", value = "代存信息", paramType = "body", dataType = "transferSubVo", dataTypeClass = TransferSubVo.class, required = true) })

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 437, message = "请求失败，交易密码错误"),
            @ApiResponse(code = 442, message = "请求失败，该会员不是您的下线,无法进行当前操作"),
            @ApiResponse(code = 411, message = "请求失败，提交参数格式有误"),
            @ApiResponse(code = 412, message = "请求失败，用户余额不足")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper transferSubMember(HttpServletResponse response, HttpServletRequest request, @RequestBody TransferSubVo transferSubVo) {

        Assert.notNull(transferSubVo,"Object cannot be empty");
        String agent = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        String member = transferSubVo.getAccount();
        String password = transferSubVo.getPassword();
        String type = transferSubVo.getType();
        Integer source = transferSubVo.getSource();
        BigDecimal amount = transferSubVo.getAmount();
        String remarks = transferSubVo.getRemarks();
        if (!RegxUtils.checkAmount(amount) || StringUtils.isEmpty(type)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        boolean success = agentTransferSubManager.checkTradePwd(agent, password);
        if(!success) {
            return JsonWrapper.failureWrapper(ResultCode._437.getCode(), ResultCode._437.getMessage());
        }
        success = agentTransferSubManager.checkSubMember(agent, member);
        if(!success) {
            return JsonWrapper.failureWrapper(ResultCode._442.getCode(), ResultCode._442.getMessage());
        }
        success = agentTransferSubManager.checkBalance(type, agent, amount);
        if(!success) {
            return JsonWrapper.failureWrapper(ResultCode._412.getCode(), ResultCode._412.getMessage());
        }
        String ip = IPUtils.getIpAddr(request);
        String orderNo = agentTransferSubManager.createTransferOrder(source, agent, member, type, amount, remarks, ip);
        return JsonWrapper.successWrapper(orderNo, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取代存记录")

    @RequestMapping(value = "/agent/transfer/records", produces = "application/json; charset=UTF-8", method = { RequestMethod.GET })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "page", value = "页码", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "account", value = "会员账号", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "okStatus", value = "状态（1、成功 2、失败）", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "startTime", value = "开始时间（默认本月第一天 2020-01-01）", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间 (默认本月最后一天  2020-01-31)", paramType = "query", dataType = "String", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, identity="agent", rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getTransferMemberRecords(HttpServletResponse response, HttpServletRequest request,String account, Integer okStatus, Integer page, Integer pageSize, String startTime, String endTime) {

        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        String agent = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        PageData pageData = agentTransferSubManager.getAgentTransferSubOrders(agent, account, okStatus, page, pageSize, startTime, endTime);
        return JsonWrapper.successWrapper(pageData, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

}
