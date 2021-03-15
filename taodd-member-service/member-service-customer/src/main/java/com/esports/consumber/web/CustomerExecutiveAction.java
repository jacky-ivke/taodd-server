package com.esports.consumber.web;

import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.consumber.dto.ConfigInfoDto;
import com.esports.consumber.service.CustomerExecutiveManager;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.netflix.discovery.converters.Auto;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = {"【会员管理系统-客服相关接口】"})
@RestController
public class CustomerExecutiveAction {


    @Autowired
    private CustomerExecutiveManager customerExecutiveManager;

    @ApiOperation("根据分类获取客服配置信息")

    @RequestMapping(value = "customer/config", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "客服分类【1、PC端会员盘口客服，2、加入合营咨询客服，3、代理平台客服，4、APP端会员盘口客服】", paramType = "query", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = false, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getCurrentReleaseVersion(HttpServletResponse response, HttpServletRequest request, String type) {

        List<ConfigInfoDto> dtos = customerExecutiveManager.getCustomers(type);
        return JsonWrapper.successWrapper(dtos, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
