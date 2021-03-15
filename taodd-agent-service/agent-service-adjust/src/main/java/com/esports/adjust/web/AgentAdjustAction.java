package com.esports.adjust.web;

import com.esports.adjust.dto.AgentAdjustDto;
import com.esports.adjust.service.AgentAdjustManager;
import com.esports.adjust.web.vo.AdjustVo;
import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.constant.CommonCode;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.PageData;
import com.esports.utils.RegxUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = {"【代理管理系统-调线相关接口】"})
@RestController
public class AgentAdjustAction {

    @Autowired
    private AgentAdjustManager agentAdjustManager;

    @ApiOperation("获取代理调线记录")

    @RequestMapping(value = "/agent/adjust/records", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "page", value = "页码", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "account", value = "会员账号", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "okStatus", value = "(0、审核中，1、成功，2、拒绝)", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "startTime", value = "开始时间（格式举例: 2020-01-01）", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间(格式举例:  2020-01-31)", paramType = "query", dataType = "String", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getGameRecord(HttpServletResponse response, HttpServletRequest request, String account, Integer page, Integer okStatus, Integer pageSize, String startTime, String endTime) {

        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        String agent = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        PageData data = agentAdjustManager.getAdjustRecords(agent, account, okStatus, page, pageSize, startTime, endTime);
        return JsonWrapper.successWrapper(data, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("代理调线申请")

    @RequestMapping(value = "/agent/adjust", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "adjustVo", value = "调线对象", paramType = "body", dataType = "adjustVo", dataTypeClass = AdjustVo.class, required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 304, message = "请求失败, 调整会员资料正在审核中,请不要重复提交申请")
    })

    @Auth(authentication = true, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper saveAgentAdjust(HttpServletResponse response, HttpServletRequest request, @RequestBody AdjustVo adjustVo) {

        Assert.notNull(adjustVo, "Object cannot be empty");
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        String member = adjustVo.getAccount();
        String remarks = adjustVo.getRemarks();
        Integer source = adjustVo.getSource();
        Integer type = adjustVo.getType();
        String guideLink = adjustVo.getGuideLink();
        List<String> imgs = adjustVo.getImgs();
        if (!RegxUtils.checkAccount(account)) {
            return JsonWrapper.failureWrapper(ResultCode._300.getCode(), ResultCode._300.getMessage());
        }
        if (!RegxUtils.checkLength(guideLink)) {
            return JsonWrapper.failureWrapper(ResultCode._451.getCode(), ResultCode._451.getMessage());
        }
        if (CollectionUtils.isEmpty(imgs)) {
            return JsonWrapper.failureWrapper(ResultCode._441.getCode(), ResultCode._441.getMessage());
        }
        boolean success = agentAdjustManager.checkAdjustMember(account, member);
        if (success) {
            return JsonWrapper.failureWrapper(ResultCode._304.getCode(), ResultCode._304.getMessage());
        }
        success = agentAdjustManager.checkAccount(account);
        if (!success) {
            return JsonWrapper.failureWrapper(ResultCode._448.getCode(), ResultCode._448.getMessage());
        }
        agentAdjustManager.saveAgentAdjust(account, member, source, type, remarks, guideLink, imgs);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取代理调线基本信息")

    @RequestMapping(value = "/agent/adjust", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getAgentAdjust(HttpServletResponse response, HttpServletRequest request) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        AgentAdjustDto dto = agentAdjustManager.getAgentAdjust(account);
        return JsonWrapper.successWrapper(dto, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
