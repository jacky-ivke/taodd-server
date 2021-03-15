package com.esports.external.basic.web;

import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.annotation.VisitLimiter;
import com.esports.constant.CommonCode;
import com.esports.constant.GlobalCode;
import com.esports.external.basic.service.ExternalAppManager;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.IPUtils;
import com.esports.utils.PageData;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

@Api(tags = {"【代理管理系统-第三方应用相关接口】"})
@RestController
public class ExternalAppAction {

    @Autowired
    private ExternalAppManager externalAppManager;

    @ApiOperation("获取会员游戏平台列表")

    @RequestMapping(value = "apis", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameType", value = "游戏类型", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "page", value = "分页编号【页码从0开始】", paramType = "query", dataType = "int", required = false),
            @ApiImplicitParam(name = "pageSize", value = "分页大小【默认10】", paramType = "query", dataType = "int", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getApiList(HttpServletResponse response, HttpServletRequest request, String gameType, Integer page, Integer pageSize) {

        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        PageData data = externalAppManager.getApiList(gameType, page, pageSize);
        return JsonWrapper.successWrapper(data, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
