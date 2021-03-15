package com.esports.api.order;

import net.sf.json.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(value = "taodd-deploy", path = "/v1.0")
public interface AgentOrderService {

    /**
     * 获取会员存款金额
     */
    @RequestMapping(value = "/order/getMemberDepositAmount", method = RequestMethod.GET)
    BigDecimal getMemberDepositAmount(@RequestParam("account") String account);

    /**
     * 获取会员取款金额
     */
    @RequestMapping(value = "/order/getMemberDrawAmount", method = RequestMethod.GET)
    BigDecimal getMemberDrawAmount(@RequestParam("account") String account);

    /**
     * 获取会员输赢金额
     */
    @RequestMapping(value = "/order/getMemberProfitAmount", method = RequestMethod.GET)
    BigDecimal getMemberProfitAmount(@RequestParam("account") String account);

    /**
     * 获取代理待取款金額
     */
    @RequestMapping(value = "/order/getAgentPendingDrawAmount", method = RequestMethod.GET)
    BigDecimal getAgentPendingDrawAmount(@RequestParam("account") String account);

    /**
     * 保存会员存款订单
     */
    @RequestMapping(value = "/order/saveDepositOrder", method = RequestMethod.POST)
    JSONObject saveDepositOrder(@RequestParam("walletType") String walletType,
                                @RequestParam("bankRealName") String bankRealName,
                                @RequestParam("bankAccount") String bankAccount,
                                @RequestParam("payType") String payType,
                                @RequestParam("channelType") String channelType,
                                @RequestParam("channelName") String channelName,
                                @RequestParam("okStatus") Integer okStatus,
                                @RequestParam("source") Integer source,
                                @RequestParam("account") String account,
                                @RequestParam("amount") BigDecimal amount,
                                @RequestParam("balance") BigDecimal balance,
                                @RequestParam("ip") String ip,
                                @RequestParam("remarks") String remarks);


    /**
     * 保存会员取款订单
     */
    @RequestMapping(value = "/order/saveDrawOrder", method = RequestMethod.POST)
    String saveDrawOrder(@RequestParam("walletType") String walletType,
                         @RequestParam("okStatus") Integer okStatus,
                         @RequestParam("auditStatus") Integer auditStatus,
                         @RequestParam("costAmount") BigDecimal costAmount,
                         @RequestParam("source") Integer source,
                         @RequestParam("account") String account,
                         @RequestParam("amount") BigDecimal amount,
                         @RequestParam("balance") BigDecimal balance,
                         @RequestParam("bankRealName") String bankRealName,
                         @RequestParam("bankName") String bankName,
                         @RequestParam("bankAccount") String bankAccount,
                         @RequestParam("ip") String ip,
                         @RequestParam("remarks") String remarks,
                         @RequestParam("isAudit") Boolean isAudit);
}
