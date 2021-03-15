package com.esports.rpc;

import com.esports.basic.bean.db1.Agent;
import com.esports.basic.dto.AgentExtProfileDto;
import com.esports.basic.service.AgentCenterManager;
import com.esports.utils.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Api(tags = {"【代理管理系统-代理中心接口提供者】"})
@RestController
public class AgentServiceRpcAction {

    @Autowired
    private AgentCenterManager agentCenterManager;

    @ApiOperation("内部模块依赖暴露接口【验证代理交易密码】")
    @RequestMapping(value = "/agent/checkTradePwd", method = {RequestMethod.GET})
    public boolean checkTradePwd(String account, String password) {

        boolean result = agentCenterManager.checkTradePwd(account, password);
        return result;
    }

    @ApiOperation("内部模块依赖暴露接口【校验会员的直属代理是否正确】")
    @RequestMapping(value = "/agent/checkSubMember", method = {RequestMethod.GET})
    boolean checkSubMember(@RequestParam("leader") String leader, @RequestParam("member") String member) {

        boolean result = agentCenterManager.checkSubMember(leader, member);
        return result;
    }

    @ApiOperation("内部模块依赖暴露接口【验证玩家账号是否存在】")
    @RequestMapping(value = "/agent/checkAccount", method = {RequestMethod.GET})
    public boolean checkAccount(String account) {
        boolean result = agentCenterManager.checkAccount(account);
        return result;
    }


    @ApiOperation("内部模块依赖暴露接口【校验代理佣金钱包余额】")
    @RequestMapping(value = "/agent/checkCommissionBalance", method = RequestMethod.GET)
    public boolean checkCommissionBalance(String account, BigDecimal amount) {

        boolean result = agentCenterManager.checkCommissionBalance(account, amount);
        return result;
    }

    @ApiOperation("内部模块依赖暴露接口【校验代理代存钱包余额】")
    @RequestMapping(value = "/agent/checkOtherBalance", method = {RequestMethod.GET})
    public boolean checkOtherBalance(String account, BigDecimal amount) {

        boolean result = agentCenterManager.checkOtherBalance(account, amount);
        return result;
    }

    @ApiOperation("内部模块依赖暴露接口【获取代理下线总数】")
    @RequestMapping(value = "/agent/getTotalSubMember", method = RequestMethod.GET)
    Integer getTotalSubMember(String account) {

        Integer subMember = agentCenterManager.getTotalSubMember(account);
        return subMember;
    }

    @ApiOperation("内部模块依赖暴露接口【获取代理当月下线新增总数】")
    @RequestMapping(value = "/agent/getCurrMonthRegSubMember", method = RequestMethod.GET)
    Integer getCurrMonthRegSubMember(String account) {

        Integer subMember = agentCenterManager.getCurrMonthRegSubMember(account);
        return subMember;
    }

    @ApiOperation("内部模块依赖暴露接口【获取代理信息】")
    @RequestMapping(value = "/agent/getAccount", method = RequestMethod.GET)
    public String getAccount(String account) {
        Agent agent = agentCenterManager.findByUserName(account);
        if (null == agent) {
            return null;
        }
        String json = JsonUtil.object2String(agent);
        return json;
    }

    @ApiOperation("内部模块依赖暴露接口【获取会员信息】")
    @RequestMapping(value = "/agent/getExtData", method = RequestMethod.GET)
    public String getExtData(String account, Integer source, String ip) {

        AgentExtProfileDto dto = agentCenterManager.getExtData(account, source, ip);
        if (null == dto) {
            return null;
        }
        String json = JsonUtil.object2String(dto);
        return json;
    }

    @ApiOperation("内部模块依赖暴露接口【更新佣金钱包余额】")
    @RequestMapping(value = "/agent/updateCommissionBalance", method = RequestMethod.PUT)
    public BigDecimal updateCommissionBalance(String account, BigDecimal amount) {

        BigDecimal commissionBalance = agentCenterManager.updateCommissionBalance(account, amount);
        return commissionBalance;
    }

    @ApiOperation("内部模块依赖暴露接口【更新代存钱包余额】")
    @RequestMapping(value = "/agent/updateOtherBalance", method = RequestMethod.PUT)
    public BigDecimal updateOtherBalance(String account, BigDecimal amount) {

        BigDecimal otherBalance = agentCenterManager.updateOtherBalance(account, amount);
        return otherBalance;
    }

    @RequestMapping(value = "/agent/updateCenterBalance", method = RequestMethod.PUT)
    BigDecimal updateCenterBalance(String account, BigDecimal amount) {

        BigDecimal balance = agentCenterManager.updateCenterBalance(account, amount);
        return balance;
    }
}
