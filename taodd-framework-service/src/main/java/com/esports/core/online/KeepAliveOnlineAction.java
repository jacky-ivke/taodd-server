package com.esports.core.online;

import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = {"【系统组件-心跳检查用户在线上报口】"})
@RestController
public class KeepAliveOnlineAction {


    @ApiOperation("心跳检测上报")
    @RequestMapping(value = "socket/keepalive", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({@ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getGameRecord(HttpServletResponse response, HttpServletRequest request) {

        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
