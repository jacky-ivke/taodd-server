package com.esports.api.log;

import com.esports.api.fallback.LogServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.sql.Timestamp;

@FeignClient(value = "taodd-deploy", path = "/v1.0", fallback = LogServiceFallback.class)
public interface MemberLogService {

    /**
     * 获取最近一次登录时间
     */
    @RequestMapping(value = "/log/getLastLoginTime", method = RequestMethod.GET)
    String getLastLoginTime(@RequestParam("account") String account);

    /**
     * 保存登录日志
     */
    @RequestMapping(value = "/log/saveLoginLog", method = RequestMethod.POST)
    void saveLoginLog(@RequestParam("account") String account,
                      @RequestParam("client") String client,
                      @RequestParam("platform") Integer platform,
                      @RequestParam("ip") Long ip);

    /**
     * 保存操作事件录日志
     */
    @RequestMapping(value = "/log/saveEventLog", method = RequestMethod.POST)
    void saveEventLog(@RequestParam("account") String account,
                      @RequestParam("type") String type,
                      @RequestParam("source") String source,
                      @RequestParam("target") String target,
                      @RequestParam("ip") Long ip);

    /**
     * 保存账单录日志
     */
    @RequestMapping(value = "/log/saveTradeLog", method = RequestMethod.POST)
    void saveTradeLog(@RequestParam("account") String account,
                      @RequestParam("logType") String logType,
                      @RequestParam("secondType") String secondType,
                      @RequestParam("okStatus") Integer okStatus,
                      @RequestParam("amount") BigDecimal amount,
                      @RequestParam("orderNo") String orderNo,
                      @RequestParam("balance") BigDecimal balance,
                      @RequestParam("ip") String ip,
                      @RequestParam("remarks") String remarks,
                      @RequestParam("postscript") String postscript);

    /**
     * 保存游戏记录日志
     */
    @RequestMapping(value = "/log/savePlayGameLog", method = RequestMethod.POST)
    public void savePlayGameLog(@RequestParam("account") String account,
                                @RequestParam("apiCode") String apiCode,
                                @RequestParam("gameCode") String gameCode,
                                @RequestParam("ip") Long ip);
}
