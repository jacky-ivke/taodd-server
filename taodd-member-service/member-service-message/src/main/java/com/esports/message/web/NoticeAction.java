package com.esports.message.web;


import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.constant.CommonCode;
import com.esports.constant.GlobalCode;
import com.esports.message.service.NoticeManager;
import com.esports.message.web.vo.NoticeVo;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import com.esports.utils.PageData;
import io.swagger.annotations.*;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = {"【会员管理系统-站内信、公告相关接口】"})
@RestController
public class NoticeAction {

    @Autowired
    private NoticeManager noticeManager;


    @ApiOperation("获取会员系统发布的公告")

    @RequestMapping(value = "advise", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌(用来标记会员读取公告)", paramType = "header", dataType = "String", required = false),
            @ApiImplicitParam(name = "type", value = "类型(0、弹出公告 1、滚动公告)", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "page", value = "分页编号【页码从0开始】", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "pageSize", value = "分页大小【默认10】", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "startTime", value = "开始时间（格式: 2020-01-01）", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间(格式： 2020-01-31)", paramType = "query", dataType = "String", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = false, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getAdvise(HttpServletResponse response, HttpServletRequest request, Integer type, Integer page, Integer pageSize, String startTime, String endTime) {

        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        Object object = request.getAttribute(CommonCode.JWT_USERNAME);
        String account = String.valueOf(null != object ? object : "");
        Integer terminal = GlobalCode.getSourceTerminal(request);
        PageData data = noticeManager.getAdviseMsg(terminal, type, account, page, pageSize, startTime, endTime);
        return JsonWrapper.successWrapper(data, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取会员发布公告详情数据")

    @RequestMapping(value = "getAdviseDetail", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "公告ID", paramType = "query", dataType = "long", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 411, message = "请求失败, 请求参数不合法")
    })

    @Auth(authentication = false, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getAdviseDetail(HttpServletResponse response, HttpServletRequest request, Long id) {

        if (null == id) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        String content = noticeManager.getNoticesDetail(id);
        return JsonWrapper.successWrapper(content, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取会员站内消息")

    @RequestMapping(value = "notices", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "page", value = "分页编号【页码从0开始】", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "pageSize", value = "分页大小【默认10】", paramType = "query", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "startTime", value = "开始时间（格式: 2020-01-01）", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间(格式： 2020-01-31)", paramType = "query", dataType = "String", required = false)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getNotices(HttpServletResponse response, HttpServletRequest request, Integer page, Integer pageSize, String startTime, String endTime) {

        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        page = PageData.getPagOrDefault(page);
        pageSize = PageData.getPageSizeOrDefault(pageSize);
        Integer terminal = GlobalCode.getSourceTerminal(request);
        PageData data = noticeManager.getMessage(terminal, account, page, pageSize, startTime, endTime);
        return JsonWrapper.successWrapper(data, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("获取会员站内消息详情")

    @RequestMapping(value = "getNoticesDetail", produces = "application/json; charset=UTF-8", method = {RequestMethod.GET})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "id", value = "公告ID", paramType = "query", dataType = "long", required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 411, message = "请求失败, 请求参数不合法")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getNoticesDetail(HttpServletResponse response, HttpServletRequest request, Long id) {

        if (null == id) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        String content = noticeManager.getNoticesDetail(id, account);
        return JsonWrapper.successWrapper(content, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("删除会员站内信/公告消息")

    @RequestMapping(value = "notices", produces = "application/json; charset=UTF-8", method = {RequestMethod.DELETE})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "noticeVo", value = "消息", paramType = "body", dataType = "noticeVo", dataTypeClass = NoticeVo.class, required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper deletes(HttpServletResponse response, HttpServletRequest request, @RequestBody NoticeVo noticeVo) {

        Assert.notNull(noticeVo, "Object cannot be empty");
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        String[] strIds = noticeVo.getIds();
        if (null == strIds || strIds.length < 0) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        Long[] ids = (Long[]) ConvertUtils.convert(strIds, Long.class);
        noticeManager.deleteMessage(ids, account);
        return JsonWrapper.successWrapper(null, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }

    @ApiOperation("标记已读会员站内信/公告消息")

    @RequestMapping(value = "notices", produces = "application/json; charset=UTF-8", method = {RequestMethod.PUT})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "noticeVo", value = "消息", paramType = "body", dataType = "noticeVo", dataTypeClass = NoticeVo.class, required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper update(HttpServletResponse response, HttpServletRequest request, @RequestBody NoticeVo noticeVo) {

        Assert.notNull(noticeVo, "Object cannot be empty");
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        String[] strIds = noticeVo.getIds();
        if (null == strIds || strIds.length < 0) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        Long[] ids = (Long[]) ConvertUtils.convert(strIds, Long.class);
        noticeManager.readMessage(ids, account);
        return JsonWrapper.successWrapper(null, ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}