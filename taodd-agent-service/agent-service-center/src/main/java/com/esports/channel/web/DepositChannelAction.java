package com.esports.channel.web;


import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.channel.dto.DepositChannelDto;
import com.esports.channel.service.DepositChannelManager;
import com.esports.constant.CommonCode;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Api(tags = {"【代理管理系统-存取渠道相关接口】"})
@RestController
public class DepositChannelAction {

    @Autowired
    private DepositChannelManager depositChannelManager;

    @ApiOperation("获取代理支付渠道列表")

    @RequestMapping(value = "/agent/depositchannels", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功,返水渠道列表")
    })

    @Auth(authentication = true, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getChannels(HttpServletResponse response, HttpServletRequest request) {

        String account = request.getAttribute(CommonCode.JWT_USERNAME).toString();
        List<DepositChannelDto> list = depositChannelManager.getChannels(account);
        return JsonWrapper.successWrapper(list, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取代理支付渠道类型")

    @RequestMapping(value = "/agent/depositchannel/types", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({})

    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getChannelTypes(HttpServletResponse response, HttpServletRequest request) {

        List<Map<String, Object>> list = depositChannelManager.getChannelTypes();
        return JsonWrapper.successWrapper(list, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取代理存款订单类型")

    @RequestMapping(value = "/agent/deposit/types", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getDepositTypes(HttpServletResponse response, HttpServletRequest request) {

        List<Map<String, Object>> list = depositChannelManager.getDepositTypes();
        return JsonWrapper.successWrapper(list, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
