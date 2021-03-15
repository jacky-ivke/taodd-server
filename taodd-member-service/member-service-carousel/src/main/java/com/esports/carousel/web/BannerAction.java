package com.esports.carousel.web;


import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.annotation.VisitLimiter;
import com.esports.carousel.service.BannerManager;
import com.esports.constant.GlobalCode;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.PageData;
import io.swagger.annotations.*;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = {"【会员管理系统-轮播、导航相关接口】"})
@RestController
public class BannerAction {

    @Autowired
    private BannerManager bannerManager;

    @ApiOperation("获取所有轮播图配置数据")

    @RequestMapping(value = "banners", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 411, message = "请求失败，参数不能为空")
    })
    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true), visitLimiter = @VisitLimiter(enabled = true))
    public JsonWrapper getBanners(HttpServletResponse response, HttpServletRequest request, String navCode, Integer page, Integer pageSize) {
        if (StringUtils.isEmpty(navCode)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        Integer terminal = GlobalCode.getSourceTerminal(request);
        JSONArray jsonArray = bannerManager.getBanners(terminal);
//        PageData pageData = bannerManager.getBanners(navCode, terminal,page, pageSize);
        return JsonWrapper.successWrapper(jsonArray, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
