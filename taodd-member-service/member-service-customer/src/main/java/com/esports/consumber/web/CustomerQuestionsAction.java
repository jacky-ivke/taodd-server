package com.esports.consumber.web;

import com.esports.annotation.Auth;
import com.esports.annotation.DynamicRateLimiter;
import com.esports.constant.CommonCode;
import com.esports.consumber.bean.db1.CustomerQuestions;
import com.esports.consumber.service.CustomerQuestionsManager;
import com.esports.consumber.web.vo.QuestionsVo;
import com.esports.response.JsonWrapper;
import com.esports.response.ResultCode;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = {"【会员管理系统-客服相关接口】"})
@RestController
public class CustomerQuestionsAction {

    @Autowired
    private CustomerQuestionsManager customerQuestionsManager;

    @ApiOperation("提交咨询信息")

    @RequestMapping(value = "customer/questions", produces = "application/json; charset=UTF-8", method = {RequestMethod.POST})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "认证令牌", paramType = "header", dataType = "String", required = true),
            @ApiImplicitParam(name = "questionsVo", value = "咨询内容", paramType = "body", dataType = "questionsVo", dataTypeClass = QuestionsVo.class, required = true)})

    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功")
    })

    @Auth(authentication = true, domain = true, userlocked = true, rateLimiter = @DynamicRateLimiter(enabled = true))
    public JsonWrapper getCurrentReleaseVersion(HttpServletResponse response, HttpServletRequest request, @RequestBody QuestionsVo questionsVo) {

        Assert.notNull(questionsVo, "Object cannot be empty");
        String mobile = questionsVo.getMobile();
        String account = String.valueOf(request.getAttribute(CommonCode.JWT_USERNAME));
        if (StringUtils.isEmpty(mobile)) {
            return JsonWrapper.failureWrapper(ResultCode._411.getCode(), ResultCode._411.getMessage());
        }
        CustomerQuestions questions = new CustomerQuestions();
        BeanUtils.copyProperties(questionsVo, questions);
        questions.setAccount(account);
        customerQuestionsManager.saveQuestion(questions);
        return JsonWrapper.successWrapper(ResultCode._200.getCode(), ResultCode._200.getMessage());
    }
}
