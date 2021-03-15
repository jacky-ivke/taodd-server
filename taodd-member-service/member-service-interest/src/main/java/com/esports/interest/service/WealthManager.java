package com.esports.interest.service;


import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.esports.api.center.MemberService;
import com.esports.api.log.MemberLogService;
import com.esports.constant.CapitalCode;
import com.esports.constant.GlobalCode;
import com.esports.constant.OrderCode;
import com.esports.interest.bean.db1.Wealth;
import com.esports.interest.bean.db1.WealthOrder;
import com.esports.interest.dao.db1.WealthDao;
import com.esports.interest.dao.db1.WealthOrderDao;
import com.esports.interest.dto.InterestDto;
import com.esports.interest.dto.WealthDto;
import com.esports.order.dto.WealthOrderDto;
import com.esports.processor.ApiGateway;
import com.esports.utils.DateUtils;
import com.esports.utils.JsonUtil;
import com.esports.utils.PageData;
import com.esports.utils.RandomUtil;
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
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Service
public class WealthManager {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberLogService memberLogService;

    @Autowired
    private WealthDao wealthDao;

    @Autowired
    private WealthOrderDao wealthOrderDao;

    private final static String WEALTH_ORDER = "【投资理财】操作者：%s";

    private final static String WEALTH_PROFIT = "【领取收益】操作者：%s 利息收益：%s";

    /**
     * 最低限制金额
     */
    private final static BigDecimal MIN_AMOUNT_LIMIT = new BigDecimal("100");

    /**
     * 未到期
     */
    private final static Integer WEALTH_UNEXPIRED = 0;

    /**
     * 领取
     */
    private final static Integer WEALTH_RECEIVE = 1;

    /**
     * 到期
     */
    private final static Integer WEALTH_EXPIRED = 2;

    public boolean checkBalance(String account, BigDecimal amount) {
        boolean success = true;
        BigDecimal balance = memberService.getBalance(account);
        if (amount.compareTo(balance) > 0) {
            success = false;
        }
        return success;
    }

    public boolean checkMinAmount(BigDecimal amount) {
        boolean success = true;
        if (MIN_AMOUNT_LIMIT.compareTo(amount) > 0) {
            success = false;
        }
        return success;
    }

    public boolean checkStatus(String orderNo) {
        boolean success = false;
        WealthOrder order = wealthOrderDao.findByOrderNo(orderNo);
        if (null == order) {
            return false;
        }
        if (null != order.getReceive() && !order.getReceive().booleanValue()) {
            success = true;
        }
        return success;
    }

    public InterestDto getInterestReport(String account) {
        Map<String, Object> map = wealthOrderDao.getInterestReport(account);
        if (CollectionUtils.isEmpty(map)) {
            return null;
        }
        InterestDto dto = JsonUtil.map2Object(map, InterestDto.class);
        return dto;
    }

    public JSONObject getValidRecords(String account, Integer page, Integer pageSize, Integer status, String startTime, String endTime) {
        startTime = StringUtils.isEmpty(startTime) ? "" : DateUtils.getDayStartTime(startTime);
        endTime = StringUtils.isEmpty(endTime) ? "" : DateUtils.getDayEndTime(endTime);
        List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
        JSONObject json = new JSONObject();
        FutureTask<Integer> t1 = this.getValidSummary(json, account, startTime, endTime);
        FutureTask<Integer> t2 = this.getValidOrders(json, account, status, page, pageSize, startTime, endTime);
        tasks.add(t1);
        tasks.add(t2);
        ApiGateway.start(tasks);
        for (FutureTask<Integer> future : tasks) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    protected FutureTask<Integer> getValidSummary(final JSONObject json, final String account, final String startTime, final String endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Map<String, Object> map = wealthOrderDao.getValidSummary(account, startTime, endTime);
                synchronized (json) {
                    json.putAll(map);
                }
                return 1;
            }
        });
        return futureTask;
    }


    protected FutureTask<Integer> getValidOrders(final JSONObject json, final String account, final Integer okStatus, final Integer page, final Integer pageSize, final String startTime, final String endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                PageData pageData = getValidOrders(account, page, pageSize, okStatus, startTime, endTime);
                if (null != pageData) {
                    synchronized (json) {
                        JSONObject obj = (JSONObject) JSONObject.toJSON(pageData);
                        json.putAll(obj);
                    }
                }
                return 1;
            }
        });
        return futureTask;
    }

    public PageData getValidOrders(String account, Integer page, Integer pageSize, Integer okStatus, String startTime, String endTime) {
        Sort.Order[] orders = {Sort.Order.desc("id"), Sort.Order.desc("createTime")};
        Sort sort = Sort.by(Arrays.asList(orders));
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Specification<WealthOrder> spec = new Specification<WealthOrder>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<WealthOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 增加筛选条件
                Predicate predicate = cb.conjunction();
                if (!StringUtils.isEmpty(account)) {
                    predicate.getExpressions().add(cb.equal(root.get("account").as(String.class), account));
                }
                if (!StringUtils.isEmpty(startTime)) {
                    Timestamp start = DateUtils.getDayStartTime(DateUtils.stringFormatDate(startTime));
                    predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("createTime").as(Timestamp.class), start));
                }
                if (!StringUtils.isEmpty(endTime)) {
                    Timestamp end = DateUtils.getDayEndTime(DateUtils.stringFormatDate(endTime));
                    predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("createTime").as(Timestamp.class), end));
                }
                Timestamp now = new Timestamp(System.currentTimeMillis());
                if (WEALTH_UNEXPIRED.equals(okStatus)) {
                    //未到期
                    predicate.getExpressions().add(cb.lessThan(root.get("approvalTime").as(Timestamp.class), now));
                } else if (WEALTH_EXPIRED.equals(okStatus)) {
                    //已到期
                    predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("approvalTime").as(Timestamp.class), now));
                }
                predicate.getExpressions().add(cb.equal(root.get("receive").as(Boolean.class), Boolean.FALSE));
                return predicate;
            }
        };
        Page<WealthOrder> pages = wealthOrderDao.findAll(spec, pageable);
        List<WealthOrderDto> list = this.assembleOrderData(pages);
        PageData pageData = PageData.builder(pages);
        pageData.setContents(list);
        return pageData;
    }

    public PageData getOrders(String account, Boolean receive, Integer page, Integer pageSize) {
        Sort.Order[] orders = {Sort.Order.desc("id"), Sort.Order.desc("createTime")};
        Sort sort = Sort.by(Arrays.asList(orders));
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Specification<WealthOrder> spec = new Specification<WealthOrder>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<WealthOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 增加筛选条件
                Predicate predicate = cb.conjunction();
                if (!StringUtils.isEmpty(account)) {
                    predicate.getExpressions().add(cb.equal(root.get("account").as(String.class), account));
                }
                if (null != receive) {
                    predicate.getExpressions().add(cb.equal(root.get("receive").as(Boolean.class), receive));
                }
                return predicate;
            }
        };
        Page<WealthOrder> pages = wealthOrderDao.findAll(spec, pageable);
        List<WealthOrderDto> list = this.assembleOrderData(pages);
        PageData pageData = PageData.builder(pages);
        pageData.setContents(list);
        return pageData;
    }

    public PageData getWealthScheme(Integer page, Integer pageSize) {
        Sort.Order[] orders = {Sort.Order.asc("priority"), Sort.Order.asc("days"),};
        Sort sort = Sort.by(Arrays.asList(orders));
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Specification<Wealth> spec = new Specification<Wealth>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Wealth> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 增加筛选条件
                Predicate predicate = cb.conjunction();
                predicate.getExpressions().add(cb.equal(root.get("okStatus").as(Integer.class), GlobalCode._ENABLE.getCode()));
                return predicate;
            }
        };
        Page<Wealth> pages = wealthDao.findAll(spec, pageable);
        List<WealthDto> list = this.assembleData(pages);
        PageData pageData = PageData.builder(pages);
        pageData.setContents(list);
        return pageData;
    }

    protected Integer handlerStatus(Timestamp approvalTime, Timestamp receiveTime) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Integer status = WEALTH_UNEXPIRED;
        if (null == receiveTime && approvalTime.before(now)) {
            status = WEALTH_EXPIRED;
        } else if (null != receiveTime) {
            return WEALTH_RECEIVE;
        }
        return status;
    }

    protected BigDecimal handlerProfit(Timestamp approvalTime, Timestamp receiveTime, BigDecimal amount) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        BigDecimal profitAmount = BigDecimal.ZERO;
        if ((null == receiveTime && null != approvalTime && approvalTime.before(now)) || (null != receiveTime && null != approvalTime && approvalTime.before(receiveTime))) {
            profitAmount = amount;
        }
        return profitAmount;
    }


    public List<WealthOrderDto> assembleOrderData(Page<WealthOrder> pages) {
        List<WealthOrderDto> dtos = new ArrayList<WealthOrderDto>();
        List<WealthOrder> list = pages.getContent();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        WealthOrderDto dto = null;
        Iterator<WealthOrder> iterator = list.iterator();
        while (iterator.hasNext()) {
            WealthOrder order = iterator.next();
            dto = new WealthOrderDto();
            Integer okStatus = this.handlerStatus(order.getApprovalTime(), order.getReceiveTime());
            dto.setOkStatus(okStatus);
            dto.setTitle(order.getTitle());
            dto.setReceive(order.getReceive());
            dto.setOrderNo(order.getOrderNo());
            dto.setDays(order.getDays());
            dto.setAmount(order.getAmount());
            dto.setRate(order.getRate());
            dto.setProfit(this.handlerProfit(order.getApprovalTime(), order.getReceiveTime(), order.getProfit()));
            dto.setCreateTime(order.getCreateTime());
            dto.setApprovalTime(order.getApprovalTime());
            dto.setReceiveTime(order.getReceiveTime());
            dtos.add(dto);
        }
        return dtos;
    }

    public List<WealthDto> assembleData(Page<Wealth> pages) {
        List<WealthDto> dtos = new ArrayList<WealthDto>();
        List<Wealth> list = pages.getContent();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        WealthDto dto = null;
        for (Wealth wealth : list) {
            dto = new WealthDto();
            dto.setPackageId(wealth.getId());
            dto.setDays(wealth.getDays());
            dto.setRate(wealth.getRate());
            dto.setTitle(wealth.getTitle());
            dtos.add(dto);
        }
        return dtos;
    }

    @LcnTransaction
    public String createOrder(String account, Long packageId, BigDecimal amount, String ip) {
        Wealth wealth = wealthDao.getOne(packageId);
        BigDecimal rate = null != wealth ? wealth.getRate() : BigDecimal.ZERO;
        Integer days = null != wealth ? wealth.getDays() : 1;
        String title = null != wealth ? wealth.getTitle() : "";
        String orderNo = RandomUtil.getUUID("");
        WealthOrder order = new WealthOrder();
        Integer okStatus = OrderCode._SUCCESS.getCode();
        String remarks = String.format(WEALTH_ORDER, account);
        BigDecimal profit = amount.multiply(rate.divide(new BigDecimal("100"))).multiply(new BigDecimal(days)).setScale(2, BigDecimal.ROUND_DOWN);
        order.setOkStatus(okStatus);
        order.setAccount(account);
        order.setOrderNo(orderNo);
        order.setAmount(amount);
        order.setDays(days);
        order.setTitle(title);
        order.setRate(rate);
        order.setProfit(profit);
        order.setRemarks(remarks);
        order.setReceive(Boolean.FALSE);
        order.setApprovalTime(new Timestamp(DateUtils.getDayOfSubDay(new Date(), days).getTime()));
        order.setCreateTime(new Timestamp(System.currentTimeMillis()));
        wealthOrderDao.save(order);
        BigDecimal balance = memberService.updateBalanceAndInterest(account, amount.negate(), amount);
        String logType = CapitalCode._INTEREST.getCode();
        String secondType = CapitalCode._INTEREST_PLAN.getCode();
        String pos = CapitalCode._INTEREST.getMessage();
        memberLogService.saveTradeLog(account, logType, secondType, okStatus, amount, orderNo, balance, ip, remarks, pos);
        return orderNo;
    }

    @LcnTransaction
    public void drawOrder(String[] orders, String account, String ip) {
        if (null == orders || orders.length < 1) {
            return;
        }
        for (String orderNo : orders) {
            boolean success = this.checkStatus(orderNo);
            if (!success) {
                continue;
            }
            this.updateOrder(orderNo, account, ip);
        }
    }

    @LcnTransaction
    private void updateOrder(WealthOrder order, String account, String ip){
        Integer okStatus = OrderCode._SUCCESS.getCode();
        Timestamp receiveTime = new Timestamp(System.currentTimeMillis());
        Timestamp approvalTime = order.getApprovalTime();
        // 提前领取不计收益
        BigDecimal profit = approvalTime.before(receiveTime) ? order.getProfit() : BigDecimal.ZERO;
        BigDecimal amount = order.getAmount();
        String orderNo = order.getOrderNo();
        BigDecimal balance = amount.add(profit);
        BigDecimal interest = amount.negate();
        String logType = CapitalCode._INTEREST.getCode();
        String secondType = CapitalCode._INTEREST_PROFIT.getCode();
        String postscript = CapitalCode._INTEREST.getMessage();
        String remarks = String.format(WEALTH_PROFIT, account, profit);
        order.setOkStatus(okStatus);
        order.setReceive(Boolean.TRUE);
        order.setReceiveTime(receiveTime);
        order.setRemarks(remarks);
        wealthOrderDao.save(order);
        balance = memberService.updateBalanceAndInterest(account, balance, interest);
        memberLogService.saveTradeLog(account, logType, secondType, okStatus, amount, orderNo, balance, ip, remarks, postscript);
    }

    public void updateOrder(String orderNo, String account, String ip) {
        boolean success = false;
        WealthOrder order = wealthOrderDao.findByOrderNo(orderNo);
        if (null == order || order.getReceive()) {
            return;
        }
        this.updateOrder(order, account, ip);
    }

    //自动发放已到期可领取的理财订单
    public void autoReceive(final String account, final String ip){
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                synchronized (account){
                    List<WealthOrder> orders = wealthOrderDao.getDueOrders(account);
                    if(CollectionUtils.isEmpty(orders)){
                        return 0;
                    }
                    for(WealthOrder order : orders){
                        updateOrder(order,account,ip);
                    }
                }
                return 1;
            }
        });
        ApiGateway.executorService.submit(futureTask);
    }

    @LcnTransaction
    public String textTx(){
        memberService.updateBalance("jacky000", new BigDecimal("8"));
        int ex = 10 / 0;
        return "hello world";
    }
}
