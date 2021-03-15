package com.esports.order.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.esports.api.log.MemberLogService;
import com.esports.constant.CapitalCode;
import com.esports.constant.PlayerCode;
import com.esports.order.bean.db1.DrawOrder;
import com.esports.order.dao.db1.DrawOrderDao;
import com.esports.order.dto.DrawRecordDto;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Service
public class DrawOrderManager {

    @Autowired
    private DrawOrderDao drawOrderDao;

    @Autowired
    private MemberLogService memberLogService;

    @LcnTransaction
    public String saveDrawOrder(Integer okStatus, Integer auditStatus, BigDecimal costAmount, Integer source, String account, BigDecimal amount, BigDecimal balance,
                                String bankRealName, String bankName, String bankAccount, String ip, String remarks, Boolean isAudit){
        String orderNo = RandomUtil.getUUID("");
        DrawOrder order = new DrawOrder();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        order.setOrderNo(orderNo);
        order.setOkStatus(okStatus);
        order.setAuditStatus(auditStatus);
        order.setAccount(account);
        order.setBankRealName(bankRealName);
        order.setBankAccount(bankAccount);
        order.setBankName(bankName);
        order.setAmount(amount);
        order.setSource(source);
        order.setIdentity(PlayerCode._MEMBER.getCode());
        order.setCostAmount(costAmount);
        order.setCreateTime(now);
        order.setIp(IPUtils.ipToLong(ip));
        order.setRemarks(remarks);
        if(null != isAudit && !isAudit.booleanValue()) {
            order.setApprovalTime(now);
            order.setTransfer(Boolean.TRUE);
            order.setTransferTime(now);
        }else {
            order.setTransfer(Boolean.FALSE);
        }
        drawOrderDao.save(order);
        String type = CapitalCode._DRAW.getCode();
        String secondType = CapitalCode._DRAW_ONLINE.getCode();
        memberLogService.saveTradeLog(account, type, secondType, okStatus, amount, orderNo, balance, ip, remarks, bankName);
        return orderNo;
    }

    public PageData getOrders(String account, Integer okStatus, Integer page, Integer pageSize, String startTime, String endTime) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Map<String, Object>> pages = drawOrderDao.findAll(account, okStatus, startTime, endTime, pageable);
        PageData pageData = PageData.builder(pages);
        List<DrawRecordDto> dtos = this.assembleData((List<Map<String, Object>>) pageData.getContents());
        pageData.setContents(dtos);
        return pageData;
    }

    private List<DrawRecordDto> assembleData(List<Map<String, Object>> list) {
        List<DrawRecordDto> dtos = new ArrayList<DrawRecordDto>();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        DrawRecordDto dto = null;
        Iterator<Map<String, Object>> iterator = list.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> map = iterator.next();
            dto = JsonUtil.map2Object(map, DrawRecordDto.class);
            String bankAccount = dto.getBankAccount();
            dto.setBankAccount(NumberUtils.hideBankAccount(bankAccount));
            dtos.add(dto);
        }
        return dtos;
    }

    public JSONObject getDrawRecords(String account, Integer okStatus, Integer page, Integer pageSize, String startTime, String endTime) {
        startTime = DateUtils.getDayStartTime(startTime);
        endTime = DateUtils.getDayEndTime(endTime);
        List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
        JSONObject json = new JSONObject();
        FutureTask<Integer> t1 = this.getDrawSummary(json, account, okStatus, startTime, endTime);
        FutureTask<Integer> t2 = this.getDrawOrders(json, account, okStatus, page, pageSize, startTime, endTime);
        tasks.add(t1);
        tasks.add(t2);
        ApiGateway.syncwait(tasks);
        return json;
    }

    protected FutureTask<Integer> getDrawSummary(final JSONObject json, final String account, final Integer okStatus, final String startTime, final String endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                BigDecimal totalAmount = drawOrderDao.getDrawSummary(account, okStatus, startTime, endTime);
                synchronized (json) {
                    json.put("totalAmount", totalAmount);
                }
                return 1;
            }
        });
        return futureTask;
    }

    protected FutureTask<Integer> getDrawOrders(final JSONObject json, final String account, final Integer okStatus, final Integer page, final Integer pageSize, final String startTime, final String endTime) {
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

    public JSONObject getTodayDrawTotal(String account){
        Map<String, Object> map = drawOrderDao.getTodayDrawTotal(account);
        if(CollectionUtils.isEmpty(map)){
            return null;
        }
        JSONObject jsonObject = JSONObject.fromObject(map);
        return jsonObject;
    }

}
