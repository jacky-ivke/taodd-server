package com.esports.order.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.esports.api.log.MemberLogService;
import com.esports.constant.CapitalCode;
import com.esports.constant.PlayerCode;
import com.esports.order.bean.db1.DepositOrder;
import com.esports.order.dao.db1.DepositOrderDao;
import com.esports.processor.ApiGateway;
import com.esports.utils.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Service
public class DepositOrderManager {

    @Autowired
    private DepositOrderDao depositOrderDao;

    @Autowired
    private MemberLogService memberLogService;

    @LcnTransaction
    public JSONObject saveDepositOrder(String bankRealName, String bankAccount, String payType, String channelType, String channelName,
                                       Integer okStatus, Integer source, String account, BigDecimal amount, BigDecimal balance, String ip, String remarks){
        JSONObject json = null;
        String orderNo = this.createOrders(bankAccount,payType,channelType,channelName,okStatus, source,account,amount,balance,ip,remarks);
        if(payType.indexOf("online")>=0){
            json = this.createOnlineDepositOrder(orderNo);
        }else{
            json = this.createCompanyDepositOrder(orderNo,amount, bankRealName, bankAccount, channelName);
        }
        return json;
    }

    protected String createOrders(String bankAccount, String payType, String channelType, String channelName,
                                  Integer okStatus, Integer source, String account, BigDecimal amount, BigDecimal balance, String ip, String remarks){
        //生成随机金额
        BigDecimal postscript = amount.add(new BigDecimal(RandomUtil.randomCommon()).divide(new BigDecimal("100"))).setScale(2,BigDecimal.ROUND_DOWN);
        DepositOrder order = new DepositOrder();
        String orderNo = RandomUtil.getUUID("");
        order.setIdentity(PlayerCode._MEMBER.getCode());
        order.setOkStatus(okStatus);
        order.setOrderNo(orderNo);
        order.setAmount(amount);
        order.setAccount(account);
        order.setRandomAmount(postscript);
        order.setSource(source);
        order.setActualAmount(BigDecimal.ZERO);
        order.setChannelName(channelName);
        order.setBankAccount(bankAccount);
        order.setPayType(payType);
        order.setType(channelType);
        order.setCreateTime(new Timestamp(System.currentTimeMillis()));
        order.setIp(IPUtils.ipToLong(ip));
        order.setRemarks(remarks);
        depositOrderDao.save(order);
        String logType = CapitalCode._DEPOSIT.getCode();
        memberLogService.saveTradeLog(account, logType, payType, okStatus, amount, orderNo, balance, ip, remarks, channelName);
        return orderNo;
    }

    protected JSONObject createCompanyDepositOrder(String orderNo, BigDecimal amount, String bankRealName, String bankAccount, String channelName) {
        JSONObject json = new JSONObject();
        json.put("orderNo", orderNo);
        json.put("amount", amount);
        json.put("bankRealName", bankRealName);
        json.put("bankAccount", bankAccount);
        json.put("channelName", channelName);
        return json;
    }

    protected JSONObject createOnlineDepositOrder(String orderNo) {
        JSONObject json = new JSONObject();
        json.put("orderNo", orderNo);
        json.put("payUrl", "");
        return json;
    }

    public PageData getOrders(String account, Integer okStatus, Integer page, Integer pageSize, String startTime, String endTime) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Map<String, Object>> pages = depositOrderDao.findAll(account, okStatus, startTime, endTime, pageable);
        PageData pageData = PageData.builder(pages);
        return pageData;
    }

    public JSONObject getDepositRecords(String account, Integer okStatus, Integer page, Integer pageSize, String startTime, String endTime) {
        startTime = DateUtils.getDayStartTime(startTime);
        endTime = DateUtils.getDayEndTime(endTime);
        List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
        JSONObject json = new JSONObject();
        FutureTask<Integer> t1 = this.getDepositSummary(json, account, okStatus, startTime, endTime);
        FutureTask<Integer> t2 = this.getDepositOrders(json, account, okStatus, page, pageSize, startTime, endTime);
        tasks.add(t1);
        tasks.add(t2);
        ApiGateway.syncwait(tasks);
        return json;
    }

    protected FutureTask<Integer> getDepositSummary(final JSONObject json, final String account, final Integer okStatus, final String startTime, final String endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                BigDecimal totalAmount = depositOrderDao.getDepositSummary(account, okStatus, startTime, endTime);
                synchronized (json) {
                    json.put("totalAmount", totalAmount);
                }
                return 1;
            }
        });
        return futureTask;
    }

    protected FutureTask<Integer> getDepositOrders(final JSONObject json, final String account, final Integer okStatus, final Integer page, final Integer pageSize, final String startTime, final String endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                PageData pageData = getOrders(account, okStatus, page, pageSize, startTime, endTime);
                if (null == pageData) {
                    return 0;
                }
                synchronized (json) {
                    json.putAll(JsonUtil.object2Map(pageData));
                }
                return 1;
            }
        });
        return futureTask;
    }

    public JSONObject getTodayOnlineDepositTotal(String account){
        Map<String,Object> map = depositOrderDao.getTodayOnlineDepositTotal(account);
        if(CollectionUtils.isEmpty(map)){
            return null;
        }
        JSONObject json = JSONObject.fromObject(map);
        return json;
    }

}
