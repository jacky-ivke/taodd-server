package com.esports.basic.web;

import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.basic.dto.AppVersionDto;
import com.esports.basic.service.AppVersionManager;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = {"【会员管理系统-基础应用相关接口】"})
@RestController
public class AppVersionAction {

	@Autowired
	private AppVersionManager appVersionManager;
	
	@ApiOperation("获取当前应用发布的最新版本信息")
    
    @RequestMapping(value = "app/versions", produces = "application/json; charset=UTF-8", method = { RequestMethod.GET })
    
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "apkType", value = "apk类型", paramType = "query", dataType = "String", required = false)})
    
    @ApiResponses({
        @ApiResponse(code = 200, message = "请求成功"),
        @ApiResponse(code = 305, message = "请求失败，当前应用无新版本发布")
    })  
    
    @Auth(authentication = false, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getCurrentReleaseVersion(HttpServletResponse response, HttpServletRequest request, String apkType) {
		AppVersionDto dto = appVersionManager.getCurrentReleaseVersion(apkType);
		if(null == dto) {
			return JsonWrapper.failureWrapper(ResultCode._305.getCode(), ResultCode._305.getMessage());
		}
        return JsonWrapper.successWrapper(dto, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
