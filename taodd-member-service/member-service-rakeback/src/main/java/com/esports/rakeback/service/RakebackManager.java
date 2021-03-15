package com.esports.rakeback.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.esports.processor.ApiGateway;
import com.esports.rakeback.dto.RakebackDto;
import com.esports.rakeback.dto.WashSummaryDto;
import com.esports.utils.DateUtils;
import com.esports.utils.JsonUtil;
import com.esports.utils.PageData;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Service
public class RakebackManager {

    @Autowired
    private RakebackOrderManager rakebackOrderManager;

    @Autowired
    private RakebackDetailManager rakebackDetailManager;
    /**
     * 最低洗码限制金额
     */
    private static final BigDecimal LOW_LIMIT_AMOUNT = new BigDecimal("1");

    public boolean checkWashAmount(String account) {
        boolean success = true;
        BigDecimal amount = this.getWashTotalAmount(account);
        if (LOW_LIMIT_AMOUNT.compareTo(amount) > 0) {
            success = false;
        }
        return success;
    }

    public PageData getWashDetail(String account, String gameType, Integer pageNo, Integer pageSize) {
        List<RakebackDto> list = rakebackOrderManager.getRakebackDetail(account, gameType);
        PageData pageData = PageData.startPage(list, 0, pageNo, pageSize);
        return pageData;
    }

    public BigDecimal getWashTotalAmount(String account) {
        BigDecimal wahTotalAmount = rakebackOrderManager.getRakebackTotalAmount(account);
        return (null == wahTotalAmount ? BigDecimal.ZERO : wahTotalAmount);
    }

    @LcnTransaction
    public BigDecimal oneKeyWashCode(String account, String ip) {
        BigDecimal wahTotalAmount = rakebackOrderManager.saveMemberRake(account, ip);
        return (null == wahTotalAmount ? BigDecimal.ZERO : wahTotalAmount);
    }

    public JSONObject getWashRecords(String account, Integer page, Integer pageSize, String apiCode, String gameType, String startTime, String endTime) {
        startTime = DateUtils.getDayStartTime(startTime);
        endTime = DateUtils.getDayEndTime(endTime);
        List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
        JSONObject json = new JSONObject();
        FutureTask<Integer> t1 = this.getWashSummary(json, account, apiCode, gameType, startTime, endTime);
        FutureTask<Integer> t2 = this.getWashOrders(json, account, page, pageSize, apiCode, gameType, startTime, endTime);
        tasks.add(t1);
        tasks.add(t2);
        ApiGateway.syncwait(tasks);
        return json;
    }

    protected FutureTask<Integer> getWashSummary(final JSONObject json, final String account, final String apiCode, final String gameType, final String startTime, String endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                WashSummaryDto dto = rakebackDetailManager.geWashSummary(account, apiCode, gameType, startTime, endTime);
                if (null == dto) {
                    return 0;
                }
                synchronized (json) {
                    json.putAll(JsonUtil.object2Map(dto));
                }
                return 1;
            }
        });
        return futureTask;
    }

    protected FutureTask<Integer> getWashOrders(final JSONObject json, final String account, final Integer page, final Integer pageSize, final String apiCode,
                                                final String gameType, final String startTime, final String endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                PageData pageData = rakebackDetailManager.getOrders(account, page, pageSize, apiCode, gameType, startTime, endTime);
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
}
