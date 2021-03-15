package com.esports.api.center;

import com.esports.api.fallback.MemberServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(value = "taodd-deploy", path = "/v1.0", fallback = MemberServiceFallback.class)
public interface MemberService {

    /**
     * 获取会员当前反水方案
     */
    @RequestMapping(value = "/member/getRakeScheme", method = {RequestMethod.GET})
    String getRakeScheme(@RequestParam("gradeCode") String gradeCode);

    /**
     * 获取会员当前VIP等级
     */
    @RequestMapping(value = "/member/getVip", method = RequestMethod.GET)
    Integer getVip(@RequestParam("account") String account);

    /**
     * 获取会员当前层级编号
     */
    @RequestMapping(value = "/member/getGradeCode", method = RequestMethod.GET)
    String getGradeCode(@RequestParam("account") String account);

    /**
     * 获取会员当前反水方案编号
     */
    @RequestMapping(value = "/member/getRakeSchemeCode", method = RequestMethod.GET)
    String getRakeSchemeCode(@RequestParam("account") String account);

    /**
     * 获取会员信息
     */
    @RequestMapping(value = "/member/getAccount", method = RequestMethod.GET)
    String getAccount(@RequestParam("account") String account);

    /**
     * 获取会员信息
     */
    @RequestMapping(value = "/member/getExtData", method = RequestMethod.GET)
    String getExtData(@RequestParam("account") String account, @RequestParam("source") Integer source, @RequestParam("ip") String ip);

    /**
     * 获取会员好友
     */
    @RequestMapping(value = "/member/getFriends", method = RequestMethod.GET)
    String getFriends(@RequestParam("account") String account);

    /**
     * 验证会员账号是否存在
     */
    @RequestMapping(value = "/member/checkAccount", method = RequestMethod.GET)
    boolean checkAccount(@RequestParam("account") String account);

    /**
     * 获取会员当前余额
     *
     * @return
     */
    @RequestMapping(value = "/member/getBalance", method = RequestMethod.GET)
    BigDecimal getBalance(@RequestParam("account") String account);

    /**
     * 创建好友账号
     */
    @RequestMapping(value = "/member/createAccount", method = RequestMethod.POST)
    void createAccount(@RequestParam("account") String account,
                       @RequestParam("password") String password,
                       @RequestParam("inviter") String inviter,
                       @RequestParam("source") Integer source,
                       @RequestParam("ip") String ip);

    /**
     * 更新会员余额
     */
    @RequestMapping(value = "/member/updateBalance", method = RequestMethod.POST)
    BigDecimal updateBalance(@RequestParam("account") String account,
                             @RequestParam("amount") BigDecimal amount);

    /**
     * 更新会员余额和利息钱包余额
     */
    @RequestMapping(value = "/member/updateBalanceAndInterest", method = RequestMethod.POST)
    BigDecimal updateBalanceAndInterest(@RequestParam("account") String account,
                                        @RequestParam("balance") BigDecimal balance,
                                        @RequestParam("interest") BigDecimal interest);

}
