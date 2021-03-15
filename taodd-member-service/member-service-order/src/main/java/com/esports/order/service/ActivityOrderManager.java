package com.esports.order.service;


import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.esports.api.log.MemberLogService;
import com.esports.constant.CapitalCode;
import com.esports.order.bean.db1.ActivityOrder;
import com.esports.order.dao.db1.ActivityOrderDao;
import com.esports.order.dto.AwardRecordDto;
import com.esports.processor.ApiGateway;
import com.esports.utils.DateUtils;
import com.esports.utils.JsonUtil;
import com.esports.utils.PageData;
import com.esports.utils.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Slf4j
@Service
public class ActivityOrderManager {

    @Autowired
    private ActivityOrderDao activityOrderDao;

    @Autowired
    private MemberLogService memberLogService;

    public PageData getOrders(String account, Integer page, Integer pageSize, Integer okstatus, String type, String startTime, String endTime) {
        Sort.Order[] orders = {Sort.Order.desc("id"), Sort.Order.desc("createTime")};
        Sort sort = Sort.by(Arrays.asList(orders));
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Specification<ActivityOrder> spec = new Specification<ActivityOrder>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<ActivityOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 增加筛选条件
                Predicate predicate = cb.conjunction();
                if (!StringUtils.isEmpty(account)) {
                    predicate.getExpressions().add(cb.equal(root.get("account").as(String.class), account));
                }
                if (null != okstatus) {
                    predicate.getExpressions().add(cb.equal(root.get("okStatus").as(Integer.class), okstatus));
                }
                if (!StringUtils.isEmpty(type)) {
                    predicate.getExpressions().add(cb.equal(root.get("type").as(String.class), type));
                }
                if (!StringUtils.isEmpty(startTime)) {
                    Timestamp start = DateUtils.getDayStartTime(DateUtils.stringFormatDate(startTime));
                    predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("createTime").as(Timestamp.class), start));
                }
                if (!StringUtils.isEmpty(endTime)) {
                    Timestamp end = DateUtils.getDayEndTime(DateUtils.stringFormatDate(endTime));
                    predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("createTime").as(Timestamp.class), end));
                }
                return predicate;
            }
        };
        Page<ActivityOrder> pages = activityOrderDao.findAll(spec, pageable);
        PageData pageData = PageData.builder(pages);
        List<AwardRecordDto> dtos = this.assembleData((List<ActivityOrder>) pageData.getContents());
        pageData.setContents(dtos);
        return pageData;
    }

    private List<AwardRecordDto> assembleData(List<ActivityOrder> list) {
        List<AwardRecordDto> dtos = new ArrayList<AwardRecordDto>();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        AwardRecordDto dto = null;
        Iterator<ActivityOrder> iterator = list.iterator();
        while (iterator.hasNext()) {
            ActivityOrder order = iterator.next();
            dto = new AwardRecordDto();
            BeanUtils.copyProperties(order, dto);
            dtos.add(dto);
        }
        return dtos;
    }

    public BigDecimal getActivityAmount(String account, Integer okStatus, String type, String startTime, String endTime) {
        BigDecimal amount = activityOrderDao.getActiveSummary(account, okStatus, type, startTime, endTime);
        return (null == amount ? BigDecimal.ZERO : amount);
    }

    public String getLastAwardTime(String account, String atyType) {
        String lastTime = activityOrderDao.getLastAwardTime(account, atyType);
        return lastTime;
    }

    @LcnTransaction
    public String saveActivityOrder(Integer okStatus, String type, BigDecimal amount, BigDecimal balance, String account, String remarks, Boolean isAudit, String ip, String postscript) {
        ActivityOrder order = new ActivityOrder();
        String orderNo = RandomUtil.getUUID("");
        Timestamp createTime = new Timestamp(System.currentTimeMillis());
        order.setOkStatus(okStatus);
        order.setOrderNo(orderNo);
        order.setAmount(amount);
        order.setAccount(account);
        order.setBalance(balance);
        order.setType(type);
        order.setCreateTime(createTime);
        order.setRemarks(remarks);
        order.setApprovalTime(null != isAudit && !isAudit.booleanValue() ? createTime : null);
        activityOrderDao.save(order);
        String logType = CapitalCode._AWARD.getCode();
        memberLogService.saveTradeLog(account, logType, type, okStatus, amount, orderNo, balance, ip, remarks, postscript);
        return orderNo;
    }

    public JSONObject getActvitRecords(String account, Integer page, Integer pageSize, Integer okstatus, String type, String startTime, String endTime) {
        startTime = DateUtils.getDayStartTime(startTime);
        endTime = DateUtils.getDayEndTime(endTime);
        List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
        JSONObject json = new JSONObject();
        FutureTask<Integer> t1 = this.getActvitySummary(json, account, okstatus, type, startTime, endTime);
        FutureTask<Integer> t2 = this.getActvityOrders(json, account, page, pageSize, okstatus, type, startTime, endTime);
        tasks.add(t1);
        tasks.add(t2);
        ApiGateway.syncwait(tasks);
        return json;
    }

    protected FutureTask<Integer> getActvitySummary(final JSONObject json, final String account, final Integer okstatus, final String type, final String startTime, final String endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                BigDecimal totalAmount = getActivityAmount(account, okstatus, type, startTime, endTime);
                synchronized (json) {
                    json.put("totalAmount", totalAmount);
                }
                return 1;
            }
        });
        return futureTask;
    }

    protected FutureTask<Integer> getActvityOrders(final JSONObject json, final String account, final Integer page, final Integer pageSize, final Integer okstatus, final String type, final String startTime, final String endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                PageData pageData = getOrders(account, page, pageSize, okstatus, type, startTime, endTime);
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
