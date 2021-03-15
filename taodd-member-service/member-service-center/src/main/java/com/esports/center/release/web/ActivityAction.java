package com.esports.center.release.web;


import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.center.release.service.ActivityManager;
import com.esports.constant.GlobalCode;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.PageData;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Api(tags = {"【会员管理系统-会员中心相关接口】"})
@RestController
public class ActivityAction {

    @Autowired
    private ActivityManager activityManager;

    @ApiOperation("获取会员优惠活动发布列表")

    @RequestMapping(value = "activitys", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "活动类型", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "attr", value = "活动分类", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "page", value = "分页编号【页码从0开始】", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "pageSize", value = "分页大小【默认10】", paramType = "query", dataType = "Integer", required = false)})

    @ApiResponses({@ApiResponse(code = 200, message = "")})
    @Auth(authentication = true, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getActivitys(HttpServletResponse response, HttpServletRequest request, String type, String attr, Integer page, Integer pageSize) {

        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        Integer terminal = GlobalCode.getSourceTerminal(request);
        PageData data = activityManager.getActivitys(type, attr, terminal, page, pageSize);
        return JsonWrapper.successWrapper(data, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取会员优惠活动具体分类【首充、洗码等】")

    @RequestMapping(value = "activitys/type", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getActivityTypes(HttpServletResponse response, HttpServletRequest request) {

        List<Map<String, Object>> list = activityManager.getActivityTypes();
        return JsonWrapper.successWrapper(list, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取会员优惠活动综合分类【电子类、棋牌类等】")

    @RequestMapping(value = "activitys/attr", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getActivityAttrs(HttpServletResponse response, HttpServletRequest request) {

        List<Map<String, Object>> list = activityManager.getActivityAttrs();
        return JsonWrapper.successWrapper(list, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
