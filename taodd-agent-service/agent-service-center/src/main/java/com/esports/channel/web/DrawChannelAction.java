package com.esports.channel.web;


import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.channel.dto.DrawChannelDto;
import com.esports.channel.service.DrawChannelManager;
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

@Api(tags = {"【代理管理系统-存取渠道相关接口】"})
@RestController
public class DrawChannelAction {

    @Autowired
    private DrawChannelManager drawChannelManager;

    @ApiOperation("获取代理提款渠道")
    @RequestMapping(value = "/agent/drawchannels", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功,提款渠道列表")
    })

    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getChannels(HttpServletResponse response, HttpServletRequest request) {

        List<DrawChannelDto> list = drawChannelManager.getChannels();
        return JsonWrapper.successWrapper(list, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
