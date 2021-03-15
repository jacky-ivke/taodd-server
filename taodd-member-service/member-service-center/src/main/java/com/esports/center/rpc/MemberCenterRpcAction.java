package com.esports.center.rpc;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.esports.center.basic.bean.db1.Member;
import com.esports.center.basic.dto.FriendDto;
import com.esports.center.basic.dto.MemberExtProfileDto;
import com.esports.center.basic.service.MemberCenterManager;
import com.esports.utils.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@Api(tags = {"【会员管理系统-会员中心接口提供者】"})
@RestController
public class MemberCenterRpcAction {

    @Autowired
    private MemberCenterManager memberCenterManager;

    @ApiOperation("内部模块依赖暴露接口【获取会员当前反水方案】")
    @RequestMapping(value = "/member/getRakeScheme", method = {RequestMethod.GET})
    public String getRakeScheme(String gradeCode) {
        String rakeScheme = memberCenterManager.getRakeScheme(gradeCode);
        return rakeScheme;
    }

    @ApiOperation("内部模块依赖暴露接口【获取会员当前VIP等级】")
    @RequestMapping(value = "/member/getVip", method = {RequestMethod.GET})
    public Integer getVip(String account) {
        Integer vip = memberCenterManager.getVip(account);
        return vip;
    }

    @ApiOperation("内部模块依赖暴露接口【验证会员账号是否存在】")
    @RequestMapping(value = "/member/checkAccount", method = {RequestMethod.GET})
    public boolean checkAccount(String account) {
        boolean result = memberCenterManager.checkAccount(account);
        return result;
    }

    @ApiOperation("内部模块依赖暴露接口【获取会员层级】")
    @RequestMapping(value = "/member/getGradeCode", method = RequestMethod.GET)
    public String getGradeCode(String account) {
        String gradeCode = memberCenterManager.getGradeCode(account);
        return gradeCode;
    }

    @ApiOperation("内部模块依赖暴露接口【获取会员反水方案编号】")
    @RequestMapping(value = "/member/getRakeSchemeCode", method = RequestMethod.GET)
    public String getRakeSchemeCode(String account) {
        String gradeCode = memberCenterManager.getRakeSchemeCode(account);
        return gradeCode;
    }

    @ApiOperation("内部模块依赖暴露接口【获取会员信息】")
    @RequestMapping(value = "/member/getAccount", method = RequestMethod.GET)
    public String getAccount(String account) {
        Member member = memberCenterManager.findByUserName(account);
        if (null == member) {
            return null;
        }
        String json = JsonUtil.object2String(member);
        return json;
    }

    @ApiOperation("内部模块依赖暴露接口【获取会员信息】")
    @RequestMapping(value = "/member/getExtData", method = RequestMethod.GET)
    public String getExtData(String account, Integer source, String ip) {

        MemberExtProfileDto dto = memberCenterManager.getExtData(account, source, ip);
        if (null == dto) {
            return null;
        }
        String json = JsonUtil.object2String(dto);
        return json;
    }

    @ApiOperation("内部模块依赖暴露接口【获取会员好友】")
    @RequestMapping(value = "/member/getFriends", method = RequestMethod.GET)
    public String getFriends(String account) {
        List<FriendDto> list = memberCenterManager.getFriends(account);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        String arr = JsonUtil.object2String(list);
        return arr;
    }

    @ApiOperation("内部模块依赖暴露接口【获取会员当前余额】")
    @RequestMapping(value = "/member/getBalance", method = RequestMethod.GET)
    BigDecimal getBalance(String account) {
        BigDecimal balance = memberCenterManager.getBalance(account);
        return balance;
    }

    @ApiOperation("内部模块依赖暴露接口【创建会员好友账号】")
    @RequestMapping(value = "/member/createAccount", method = RequestMethod.POST)
    public void createAccount(String account, String password, String inviter, Integer source, String ip) {
        memberCenterManager.register(account, password, inviter, source, ip);
    }

    @ApiOperation("内部模块依赖暴露接口【更新会员余额】")
    @RequestMapping(value = "/member/updateBalance", method = RequestMethod.POST)
    @LcnTransaction
    public BigDecimal updateBalance(String account, BigDecimal amount) {
        BigDecimal balance = memberCenterManager.updateBalance(account, amount);
        return amount;
    }

    @ApiOperation("内部模块依赖暴露接口【更新会员余额以及利息钱包余额】")
    @RequestMapping(value = "/member/updateBalanceAndInterest", method = RequestMethod.POST)
    @LcnTransaction
    public BigDecimal updateBalanceAndInterest(String account, BigDecimal balance, BigDecimal interest) {
        BigDecimal amount = memberCenterManager.updateBalanceAndInterest(account, balance, interest);
        return amount;
    }

}
