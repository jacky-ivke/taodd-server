package com.esports.log.rpc;


import com.esports.log.service.MemberLogManager;
import com.esports.log.service.MemberLoginLogManager;
import com.esports.log.service.MemberPlayGameLogManager;
import com.esports.log.service.MemberTradeManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Api(tags = {"【会员管理系统-日志记录相关接口提供者】"})
@RestController
public class MemberLogRpcAction {

    @Autowired
    private MemberLogManager memberLogManager;

    @Autowired
    private MemberLoginLogManager memberLoginLogManager;

    @Autowired
    private MemberTradeManager memberTradeManager;

    @Autowired
    private MemberPlayGameLogManager memberPlayGameLogManager;

    @ApiOperation("内部模块依赖暴露接口【记录会员操作事件日志】")
    @RequestMapping(value = "/log/getLastLoginTime", method = RequestMethod.GET)
    public String getLastLoginTime(String account) {
        String loginTime = memberLoginLogManager.getLastLoginTime(account);
        return loginTime;
    }

    @ApiOperation("内部模块依赖暴露接口【记录会员操作事件日志】")
    @RequestMapping(value = "/log/saveEventLog", method = RequestMethod.POST)
    public void saveEventLog(String account, String type, String source, String target, Long ip) {
        if (StringUtils.isEmpty(account)) {
            return;
        }
        memberLogManager.log(account, type, source, target, ip);
    }

    @ApiOperation("内部模块依赖暴露接口【记录会员登录日志】")
    @RequestMapping(value = "/log/saveLoginLog", method = RequestMethod.POST)
    public void saveLoginLog(String account, String client, Integer platform, Long ip) {
        memberLoginLogManager.log(account, client, platform, ip);

    }

    @ApiOperation("内部模块依赖暴露接口【记录会员交易日志】")
    @RequestMapping(value = "/log/saveTradeLog", method = RequestMethod.POST)
    public void saveTradeLog(String account, String logType, String secondType, Integer okStatus, BigDecimal amount, String orderNo,
                             BigDecimal balance, String ip, String remarks, String postscript) {
        memberTradeManager.log(logType, secondType, okStatus, amount, orderNo, account, balance, ip, remarks, postscript);
    }

    @ApiOperation("内部模块依赖暴露接口【记录会员游戏日志】")
    @RequestMapping(value = "/log/savePlayGameLog", method = RequestMethod.POST)
    public void savePlayGameLog(String account, String apiCode, String gameCode, Long ip) {
        memberPlayGameLogManager.log(account, apiCode, gameCode, ip);
    }
}
