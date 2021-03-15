package com.esports.interest.web;


import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.interest.service.WealthManager;
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

@Api(tags = {"【会员管理系统-利息钱包相关接口】"})
@RestController
public class WealthAction {

	@Autowired
	private WealthManager wealthManager;

	@ApiOperation("获利息钱包理财套餐方案")
	
	@RequestMapping(value = "wealths", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET })
	
	   @ApiImplicitParams({
	        @ApiImplicitParam(name = "page", value = "分页编号【页码从0开始】", paramType = "query", dataType = "Integer", required = false),
	        @ApiImplicitParam(name = "pageSize", value = "分页大小【默认10】", paramType = "query", dataType = "Integer", required = false)})
	
	@ApiResponses({ 
			@ApiResponse(code = 200, message = "请求成功") 
	})
	
	@Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
	public JsonWrapper getSchemes(HttpServletResponse response, HttpServletRequest request, Integer page, Integer pageSize) {

		page = PageData.getPagOrDefault(page);
		pageSize = PageData.getPageSizeOrDefault(pageSize);
		PageData data = wealthManager.getWealthScheme(page, pageSize);
		return JsonWrapper.successWrapper(data, ResultCode._200.getCode(), ResultCode._200.getMessage());
	}
}
