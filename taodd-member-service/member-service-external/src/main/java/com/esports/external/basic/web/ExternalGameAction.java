package com.esports.external.basic.web;


import com.esports.annotation.Auth;
import com.esports.annotation.DistributedLock;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.constant.CommonCode;
import com.esports.constant.GlobalCode;
import com.esports.external.basic.service.ExternalGameManager;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.IPUtils;
import com.esports.utils.PageData;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = {"【会员管理系统-第三方应用相关接口】"})
@RestController
public class ExternalGameAction {

    @Autowired
    private ExternalGameManager externalGameManager;

    @ApiOperation("获取会员游戏列表")

    @RequestMapping(value = "games", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页编号【页码从0开始】", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "pageSize", value = "分页大小【默认10】", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "apiCode", value = "API编号", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "gameName", value = "游戏名称", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "type", value = "游戏类型【真人、电子、棋牌等】", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "tag", value = "游戏标签【'hot':热门游戏  'recommend':必玩推荐】", paramType = "query", dataType = "String", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = false, domain = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getGameList(HttpServletResponse response, HttpServletRequest request, String type, String tag, String apiCode, String gameName, Integer page, Integer pageSize) {

        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        Integer terminal = GlobalCode.getSourceTerminal(request);
        PageData data = externalGameManager.getGameList(type, tag, terminal, apiCode, gameName, page, pageSize);
        return JsonWrapper.successWrapper(data, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取第三方游戏打开地址")

    @RequestMapping(value = "games/openurl", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "apiCode", value = "API编号", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "gameCode", value = "游戏编号", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "gameType", value = "游戏类型", paramType = "query", dataType = "String", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 411, message = "请求失败,提交参数格式有误"),
            @ApiResponse(code = 504, message = "请求失败,API正在维护")
    })

    @Auth(authentication = true, domain = true, sync = @DistributedLock(enabled = true), rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getGameUrl(HttpServletResponse response, HttpServletRequest request, String apiCode, String gameCode, String gameType) {

        if (StringUtils.isEmpty(apiCode) || StringUtils.isEmpty(gameType)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        String account = request.getAttribute(CommonCode.JWT_USERNAME).toString();
        String userSource = request.getHeader(CommonCode.USER_SOURCE);
        Integer deviceType = GlobalCode.getSource(request);
        String ip = IPUtils.getIpAddr(request);
        Integer lang = 1;
        boolean success = externalGameManager.checkAppStatus(apiCode);
        if(!success){
            return JsonWrapper.failureWrapper(ResultCode._504.getCode(), ResultCode._504.getMessage());
        }
        String url = externalGameManager.getGameUrl(account, apiCode, gameCode, gameType, deviceType, lang, ip);
        if (StringUtils.isEmpty(url)) {
            return JsonWrapper.failureWrapper(ResultCode._501.getCode(), ResultCode._501.getMessage());
        }
        return JsonWrapper.successWrapper(url, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
