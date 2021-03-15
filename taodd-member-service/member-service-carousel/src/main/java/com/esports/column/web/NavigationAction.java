package com.esports.column.web;

import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.annotation.VisitLimiter;
import com.esports.column.dto.NavigationDto;
import com.esports.column.service.NavigationManager;
import com.esports.constant.GlobalCode;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = {"【会员管理系统-轮播、导航相关接口】"})
@RestController
public class NavigationAction {

    @Autowired
    private NavigationManager navigationManager;

    @ApiOperation("获取一级导航栏目配置数据")

    @ApiImplicitParams({})

    @RequestMapping(value = "top/navs", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getTopNavs(HttpServletResponse response, HttpServletRequest request) {

        Integer terminal = GlobalCode.getSourceTerminal(request);
        List<NavigationDto> list = navigationManager.getTopNavs(terminal);
        return JsonWrapper.successWrapper(list, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("根据父编号获取子导航配置数据")

    @RequestMapping(value = "sub/navs", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "导肮编号", paramType = "query", dataType = "String", required = true)})

    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true), visitLimiter = @VisitLimiter(enabled = true))
    public JsonWrapper getSubNavs(HttpServletResponse response, HttpServletRequest request, String code) {

        Integer termial = GlobalCode.getSourceTerminal(request);
        List<NavigationDto> list = navigationManager.getSubNavs(code, termial);
        return JsonWrapper.successWrapper(list, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取所有导航栏目配置数据")

    @RequestMapping(value = "navs", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({})

    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true), visitLimiter = @VisitLimiter(enabled = true))
    public JsonWrapper getNavs(HttpServletResponse response, HttpServletRequest request) {

        Integer termial = GlobalCode.getSourceTerminal(request);
        JSONArray jsonArray = navigationManager.getNavTree(termial);
        return JsonWrapper.successWrapper(jsonArray, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

}
