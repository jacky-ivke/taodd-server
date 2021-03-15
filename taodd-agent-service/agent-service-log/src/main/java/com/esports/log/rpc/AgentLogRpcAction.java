package com.esports.log.rpc;


import com.esports.log.service.AgentLogManager;
import com.esports.log.service.AgentLoginLogManager;
import com.esports.log.service.AgentTradeLogManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Api(tags = {"【代理管理系统-日志记录相关接口提供者】"})
@RestController
public class AgentLogRpcAction {

    @Autowired
    private AgentLogManager agentLogManager;

    @Autowired
    private AgentLoginLogManager agentLoginLogManager;

    @Autowired
    private AgentTradeLogManager agentTradeLogManager;


    @ApiOperation("内部模块依赖暴露接口【记录会员操作事件日志】")
    @RequestMapping(value = "/log/saveEventLog", method = RequestMethod.POST)
    public void saveEventLog(String account, String type, String source, String target, Long ip) {
        if (StringUtils.isEmpty(account)) {
            return;
        }
        agentLogManager.log(account, type, source, target, ip);
    }

    @ApiOperation("内部模块依赖暴露接口【记录会员登录日志】")
    @RequestMapping(value = "/log/saveLoginLog", method = RequestMethod.POST)
    public void saveLoginLog(String account, String client, Integer platform, Long ip) {
        agentLoginLogManager.log(account, client, platform, ip);

    }

    @ApiOperation("内部模块依赖暴露接口【记录代理交易日志】")
    @RequestMapping(value = "/log/saveAgentTradeLog", method = RequestMethod.POST)
    public void saveTradeLog(String walletType, String account, String logType, String secondType, Integer okStatus, BigDecimal amount, String orderNo,
                             BigDecimal balance, String ip, String remarks) {
        agentTradeLogManager.log(walletType, logType, secondType, okStatus, amount, orderNo, account, balance, ip, remarks);
    }

    @ApiOperation("内部模块依赖暴露接口【记录会员交易日志】")
    @RequestMapping(value = "/log/saveMemberTradeLog", method = RequestMethod.POST)
    public void saveTradeLog(String account, String logType, String secondType, Integer okStatus, BigDecimal amount, String orderNo, BigDecimal balance, String ip, String remarks) {


        return;
    }
}
