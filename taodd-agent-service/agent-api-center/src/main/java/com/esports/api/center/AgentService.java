package com.esports.api.center;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(value = "taodd-deploy", path = "/v1.0")
public interface AgentService {

    /**
     * 校验代理的交易密码
     */
    @RequestMapping(value = "/agent/checkTradePwd", method = {RequestMethod.GET})
    boolean checkTradePwd(@RequestParam("account") String account, @RequestParam("password") String password);

    /**
     * 校验会员的直属代理是否正确
     */
    @RequestMapping(value = "/agent/checkSubMember", method = {RequestMethod.GET})
    boolean checkSubMember(@RequestParam("leader") String leader, @RequestParam("member") String member);

    /**
     * 验证玩家账号是否存在
     */
    @RequestMapping(value = "/agent/checkAccount", method = {RequestMethod.GET})
    boolean checkAccount(@RequestParam("account") String account);

    /**
     * 验证代理佣金钱包余额
     */
    @RequestMapping(value = "/agent/checkCommissionBalance", method = {RequestMethod.GET})
    boolean checkCommissionBalance(@RequestParam("account") String account, @RequestParam("amount") BigDecimal amount);

    /**
     * 验证代理代存钱包余额
     */
    @RequestMapping(value = "/agent/checkOtherBalance", method = {RequestMethod.GET})
    boolean checkOtherBalance(@RequestParam("account") String account, @RequestParam("amount") BigDecimal amount);

    /**
     * 获取代理下线总数
     */
    @RequestMapping(value = "/agent/getTotalSubMember", method = RequestMethod.GET)
    Integer getTotalSubMember(@RequestParam("account") String account);

    /**
     * 获取代理当月下线新增总数
     */
    @RequestMapping(value = "/agent/getCurrMonthRegSubMember", method = RequestMethod.GET)
    Integer getCurrMonthRegSubMember(@RequestParam("account") String account);

    /**
     * 获取代理信息
     */
    @RequestMapping(value = "/agent/getAccount", method = RequestMethod.GET)
    String getAccount(@RequestParam("account") String account);

    /**
     * 获取代理信息
     */
    @RequestMapping(value = "/agent/getExtData", method = RequestMethod.GET)
    String getExtData(@RequestParam("account") String account, @RequestParam("source") Integer source, @RequestParam("ip") String ip);

    /**
     * 更新代理佣金钱包余额
     */
    @RequestMapping(value = "/agent/updateCommissionBalance", method = RequestMethod.PUT)
    BigDecimal updateCommissionBalance(@RequestParam("account") String account, @RequestParam("amount") BigDecimal amount);

    /**
     * 更新代理代存钱包余额
     */
    @RequestMapping(value = "/agent/updateOtherBalance", method = RequestMethod.PUT)
    BigDecimal updateOtherBalance(@RequestParam("account") String account, @RequestParam("amount") BigDecimal amount);

    /**
     * 更新代理中心钱包余额
     */
    @RequestMapping(value = "/agent/updateCenterBalance", method = RequestMethod.PUT)
    BigDecimal updateCenterBalance(@RequestParam("account") String account, @RequestParam("amount") BigDecimal amount);

}
