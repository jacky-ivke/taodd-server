package com.esports.api.order;

import net.sf.json.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(value = "taodd-deploy", path = "/v1.0")
public interface MemberOrderService {

    /**
     * 获取会奖励订单
     */
    @RequestMapping(value = "/order/getActivityOrders", method = RequestMethod.GET)
    String getActivityOrders(@RequestParam("account") String account,
                             @RequestParam("okStatus") Integer okStatus,
                             @RequestParam("type") String type,
                             @RequestParam("startTime") String startTime,
                             @RequestParam("endTime") String endTime,
                             @RequestParam("page") Integer page,
                             @RequestParam("pageSize") Integer pageSize);

    /**
     * 获取会奖励总额
     */
    @RequestMapping(value = "/order/getActivityAmount", method = RequestMethod.GET)
    BigDecimal getActivityAmount(@RequestParam("account") String account,
                                 @RequestParam("type") String type,
                                 @RequestParam("startTime") String startTime,
                                 @RequestParam("endTime") String endTime);

    /**
     * 获取最近一次奖励时间
     */
    @RequestMapping(value = "/order/getLastAwardTime", method = RequestMethod.GET)
    String getLastAwardTime(@RequestParam("account") String account,
                            @RequestParam("type") String type);

    /**
     * 获取会员投注总额
     */
    @RequestMapping(value = "/order/getBetTotalAmount", method = RequestMethod.GET)
    BigDecimal getBetTotalAmount(@RequestParam("account") String account);

    /**
     * 获取会员今日取款总况
     */
    @RequestMapping(value = "/order/getTodayDrawTotal", method = RequestMethod.GET)
    JSONObject getTodayDrawTotal(@RequestParam("account") String account);

    /**
     * 获取会员今日存款总况
     */
    @RequestMapping(value = "/order/getTodayOnlineDepositTotal", method = RequestMethod.GET)
    JSONObject getTodayOnlineDepositTotal(@RequestParam("account") String account);

    /**
     * 保存会员活动奖励订单
     */
    @RequestMapping(value = "/order/saveActivityOrder", method = RequestMethod.POST)
    String saveActivityOrder(@RequestParam("okStatus") Integer okStatus,
                             @RequestParam("type") String type,
                             @RequestParam("amount") BigDecimal amount,
                             @RequestParam("balance") BigDecimal balance,
                             @RequestParam("account") String account,
                             @RequestParam("remarks") String remarks,
                             @RequestParam("isAudit") Boolean isAudit,
                             @RequestParam("ip") String ip,
                             @RequestParam("postscript") String postscript);

    /**
     * 保存会员存款订单
     */
    @RequestMapping(value = "/order/saveDepositOrder", method = RequestMethod.POST)
    JSONObject saveDepositOrder(@RequestParam("bankRealName") String bankRealName,
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
    String saveDrawOrder(@RequestParam("okStatus") Integer okStatus,
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
