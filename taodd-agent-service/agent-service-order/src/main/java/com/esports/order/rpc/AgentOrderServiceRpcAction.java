package com.esports.order.rpc;

import com.esports.order.service.BettingOrderManager;
import com.esports.order.service.DepositOrderManager;
import com.esports.order.service.DrawOrderManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Api(tags = {"【代理管理系统-订单服务接口提供者】"})
@RestController
public class AgentOrderServiceRpcAction {

    @Autowired
    private DrawOrderManager drawOrderManager;

    @Autowired
    private DepositOrderManager depositOrderManager;

    @Autowired
    private BettingOrderManager bettingOrderManager;


    /**
     * 获取会员存款金额
     */
    @RequestMapping(value = "/order/getMemberDepositAmount", method = RequestMethod.GET)
    BigDecimal getMemberDepositAmount(String account){

        BigDecimal amount = depositOrderManager.getMemberDepositAmount(account);
        return amount;
    }

    /**
     * 获取会员取款金额
     */
    @RequestMapping(value = "/order/getMemberDrawAmount", method = RequestMethod.GET)
    BigDecimal getMemberDrawAmount(String account){

        BigDecimal amount = drawOrderManager.getMemberDrawAmount(account);
        return amount;
    }

    /**
     * 获取会员输赢金额
     */
    @RequestMapping(value = "/order/getMemberProfitAmount", method = RequestMethod.GET)
    BigDecimal getMemberProfitAmount(String account){

        BigDecimal amount = bettingOrderManager.getMemberProfitAmount(account);
        return amount;
    }

    @ApiOperation("内部模块依赖暴露接口【获取代理取款待审金额】")
    @RequestMapping(value = "/order/getAgentPendingDrawAmount", method = RequestMethod.GET)
    public BigDecimal getAgentPendingDrawAmount(String account) {

        BigDecimal amount = drawOrderManager.getAgentPendingDrawAmount(account);
        return amount;
    }

    @ApiOperation("内部模块依赖暴露接口【保存代理存款信息】")
    @RequestMapping(value = "/order/saveDepositOrder", method = RequestMethod.POST)
    public JSONObject saveDepositOrder(String walletType, String bankRealName, String bankAccount, String payType, String channelType, String channelName, Integer okStatus, Integer source, String account, BigDecimal amount, BigDecimal balance, String ip, String remarks) {

        JSONObject json = depositOrderManager.saveDepositOrder(walletType, bankRealName, bankAccount, payType, channelType, channelName, okStatus, source, account, amount, balance, ip, remarks);
        return json;
    }

    @ApiOperation("内部模块依赖暴露接口【保存代理取款信息】")
    @RequestMapping(value = "/order/saveDrawOrder", method = RequestMethod.POST)
    public String saveDrawOrder(String walletType, Integer okStatus, Integer auditStatus, BigDecimal costAmount, Integer source, String account, BigDecimal amount, BigDecimal balance,
                                String bankRealName, String bankName, String bankAccount, String ip, String remarks, Boolean isAudit) {
        String orderNo = drawOrderManager.saveDrawOrder(walletType, okStatus, auditStatus, costAmount, source, account, amount, balance, bankRealName, bankName, bankAccount, ip, remarks, isAudit);
        return orderNo;
    }
}
