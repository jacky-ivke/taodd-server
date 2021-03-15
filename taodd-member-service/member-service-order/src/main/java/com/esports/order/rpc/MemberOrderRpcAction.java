package com.esports.order.rpc;


import com.esports.order.service.ActivityOrderManager;
import com.esports.order.service.BettingOrderManager;
import com.esports.order.service.DepositOrderManager;
import com.esports.order.service.DrawOrderManager;
import com.esports.utils.JsonUtil;
import com.esports.utils.PageData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Api(tags = {"【会员管理系统-订单服务接口提供者】"})
@RestController
public class MemberOrderRpcAction {

    @Autowired
    private ActivityOrderManager activityOrderManager;

    @Autowired
    private BettingOrderManager bettingOrderManager;

    @Autowired
    private DepositOrderManager depositOrderManager;

    @Autowired
    private DrawOrderManager drawOrderManager;

    @ApiOperation("内部模块依赖暴露接口【获取活动奖励订单记录】")
    @RequestMapping(value = "/order/getActivityOrders", method = {RequestMethod.GET})
    public String getActivityOrders(String account, Integer okStatus, String type, String startTime, String endTime, Integer page, Integer pageSize) {

        PageData pageData = activityOrderManager.getOrders(account, page, pageSize, okStatus, type, startTime, endTime);
        String data = JsonUtil.object2String(pageData);
        return data;
    }

    @ApiOperation("内部模块依赖暴露接口【获取活动奖励金额】")
    @RequestMapping(value = "/order/getActivityAmount", method = RequestMethod.GET)
    public BigDecimal getActivityAmount(String account, Integer okStatus, String type, String startTime, String endTime) {

        BigDecimal amount = activityOrderManager.getActivityAmount(account, okStatus, type, startTime, endTime);
        return amount;
    }

    @ApiOperation("内部模块依赖暴露接口【根据活动类型获取会员最最近一次活动奖励时间】")
    @RequestMapping(value = "/order/getLastAwardTime", method = RequestMethod.GET)
    public String getLastAwardTime(String account, String type) {

        String lastTime = activityOrderManager.getLastAwardTime(account, type);
        return lastTime;
    }

    @ApiOperation("内部模块依赖暴露接口【获取会员投注总额】")
    @RequestMapping(value = "/order/getBetTotalAmount", method = RequestMethod.GET)
    public BigDecimal getBetTotalAmount(String account) {

        BigDecimal amount = bettingOrderManager.getBetTotalAmount(account);
        return amount;
    }

    @ApiOperation("内部模块依赖暴露接口【保存会员活动奖励信息】")
    @RequestMapping(value = "/order/saveActivityOrder", method = RequestMethod.POST)
    public String saveActivityOrder(Integer okStatus, String type, BigDecimal amount, BigDecimal balance, String account, String remarks, Boolean isAudit, String ip, String postscript) {
        String orderNo = activityOrderManager.saveActivityOrder(okStatus, type, amount, balance, account, remarks, isAudit, ip, postscript);
        return orderNo;
    }

    @ApiOperation("内部模块依赖暴露接口【保存会员存款信息】")
    @RequestMapping(value = "/order/saveDepositOrder", method = RequestMethod.POST)
    public JSONObject saveDepositOrder(String bankRealName, String bankAccount, String payType, String channelType, String channelName, Integer okStatus, Integer source, String account, BigDecimal amount, BigDecimal balance, String ip, String remarks) {

        JSONObject json = depositOrderManager.saveDepositOrder(bankRealName, bankAccount, payType, channelType, channelName, okStatus, source, account, amount, balance, ip, remarks);
        return json;
    }

    @ApiOperation("内部模块依赖暴露接口【获取会员今日取款信息】")
    @RequestMapping(value = "/order/getTodayDrawTotal", method = RequestMethod.GET)
    public JSONObject getTodayDrawTotal(String account) {

        JSONObject json = drawOrderManager.getTodayDrawTotal(account);
        return json;
    }

    @ApiOperation("内部模块依赖暴露接口【获取会员今日存款信息】")
    @RequestMapping(value = "/order/getTodayOnlineDepositTotal", method = RequestMethod.GET)
    public JSONObject getTodayOnlineDepositTotal(String account){

        JSONObject json = depositOrderManager.getTodayOnlineDepositTotal(account);
        return json;
    }

    @ApiOperation("内部模块依赖暴露接口【保存会员取款信息】")
    @RequestMapping(value = "/order/saveDrawOrder", method = RequestMethod.POST)
    public String saveDrawOrder(Integer okStatus, Integer auditStatus, BigDecimal costAmount, Integer source, String account, BigDecimal amount, BigDecimal balance,
                                String bankRealName, String bankName, String bankAccount, String ip, String remarks, Boolean isAudit) {
        String orderNo = drawOrderManager.saveDrawOrder(okStatus, auditStatus, costAmount, source, account, amount, balance, bankRealName, bankName, bankAccount, ip, remarks, isAudit);
        return orderNo;
    }
}

