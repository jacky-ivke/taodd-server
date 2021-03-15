package com.esports.api.fallback;

import com.esports.api.order.MemberOrderService;
import net.sf.json.JSONObject;

import java.math.BigDecimal;

public class OrderServiceFallback implements MemberOrderService {

    @Override
    public String getActivityOrders(String account, Integer okStatus, String type, String startTime, String endTime, Integer page, Integer pageSize) {
        return null;
    }

    @Override
    public BigDecimal getActivityAmount(String account, String type, String startTime, String endTime) {
        return null;
    }

    @Override
    public String getLastAwardTime(String account, String type) {
        return null;
    }

    @Override
    public BigDecimal getBetTotalAmount(String account) {
        return null;
    }

    @Override
    public JSONObject getTodayDrawTotal(String account) {
        return null;
    }

    @Override
    public JSONObject getTodayOnlineDepositTotal(String account) {
        return null;
    }

    @Override
    public String saveActivityOrder(Integer okStatus, String type, BigDecimal amount, BigDecimal balance, String account, String remarks, Boolean isAudit, String ip, String postscript) {

        return null;
    }

    @Override
    public JSONObject saveDepositOrder(String bankRealName, String bankAccount, String payType, String channelType, String channelName, Integer okStatus, Integer source, String account, BigDecimal amount, BigDecimal balance, String ip, String remarks) {
        return null;
    }

    @Override
    public String saveDrawOrder(Integer okStatus, Integer auditStatus, BigDecimal costAmount, Integer source, String account, BigDecimal amount, BigDecimal balance, String bankRealName, String bankName, String bankAccount, String ip, String remarks, Boolean isAudit) {
        return null;
    }
}
