package com.esports.interest.rpc;

import com.esports.api.center.MemberService;
import com.esports.interest.service.WealthManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"【会员管理系统-利息钱包业务接口提供者】"})
@RestController
public class MemberWealthRpcAction {

    @Autowired
    private WealthManager wealthManager;

    @Autowired
    private MemberService memberService;

    @ApiOperation("内部模块依赖暴露接口【自动发放已到期可领取的理财订单】")
    @RequestMapping(value = "/interest/autoReceive", method = RequestMethod.POST)
    public void autoReceive(String account, String ip) {

        wealthManager.autoReceive(account, ip);
    }


    @ApiOperation("临时接口【事务测试】")
    @RequestMapping(value = "/member/testTx", method = RequestMethod.POST)
    public String testTx(){

        return wealthManager.textTx();
    }
}

