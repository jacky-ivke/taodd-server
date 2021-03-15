package com.esports.order.service;

import com.esports.api.external.MemberExternalService;
import com.esports.order.bean.db1.BettingOrder;
import com.esports.order.dao.db1.BettingOrderDao;
import com.esports.order.dto.BetSummaryDto;
import com.esports.order.dto.BettingRecordDto;
import com.esports.order.dto.DailyReportDto;
import com.esports.processor.ApiGateway;
import com.esports.utils.DateUtils;
import com.esports.utils.JsonUtil;
import com.esports.utils.PageData;
import net.sf.json.JSONObject;
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
import java.util.concurrent.FutureTask;

@Service
public class BettingOrderManager {

    @Autowired
    private BettingOrderDao bettingOrderDao;

    @Autowired
    private MemberExternalService memberExternalService;


    public BigDecimal getBetTotalAmount(String account) {
        BigDecimal betAmount = bettingOrderDao.getBetTotalAmount(account);
        return (null == betAmount ? BigDecimal.ZERO : betAmount);
    }

    public PageData getOrders(String account, Integer page, Integer pageSize, String apiCode, String gameType, String gameName, String startTime, String endTime) {
        Sort.Order[] orders = {Sort.Order.desc("id"), Sort.Order.desc("createTime")};
        Sort sort = Sort.by(Arrays.asList(orders));
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Specification<BettingOrder> spec = new Specification<BettingOrder>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<BettingOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 增加筛选条件
                Predicate predicate = cb.conjunction();
                if (!StringUtils.isEmpty(account)) {
                    predicate.getExpressions().add(cb.equal(root.get("account").as(String.class), account));
                }
                if (!StringUtils.isEmpty(gameType)) {
                    predicate.getExpressions().add(cb.equal(root.get("gameType").as(String.class), gameType));
                }
                if (!StringUtils.isEmpty(apiCode)) {
                    predicate.getExpressions().add(cb.equal(root.get("provider").as(String.class), apiCode));
                }
                if (!StringUtils.isEmpty(gameName)) {
                    predicate.getExpressions().add(cb.like(root.get("gameName").as(String.class), "%" + gameName + "%"));
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
        Page<BettingOrder> pages = bettingOrderDao.findAll(spec, pageable);
        PageData pageData = PageData.builder(pages);
        List<BettingRecordDto> dtos = this.assembleData((List<BettingOrder>) pageData.getContents());
        pageData.setContents(dtos);
        return pageData;
    }

    private List<BettingRecordDto> assembleData(List<BettingOrder> list) {
        List<BettingRecordDto> dtos = new ArrayList<BettingRecordDto>();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        BettingRecordDto dto = null;
        Iterator<BettingOrder> iterator = list.iterator();
        while (iterator.hasNext()) {
            BettingOrder order = iterator.next();
            dto = new BettingRecordDto();
            BigDecimal betTotal = null != order.getBetTotal() ? order.getBetTotal() : BigDecimal.ZERO;
            BigDecimal betAmount = null != order.getBetAmount() ? order.getBetAmount() : BigDecimal.ZERO;
            BigDecimal profitAmount = null != order.getProfitAmount() ? order.getProfitAmount() : BigDecimal.ZERO;
            String gameName = memberExternalService.getGameName(order.getProvider(), order.getGameId());
            gameName = StringUtils.isEmpty(gameName) ? order.getGameName() : gameName;
            dto.setTransId(order.getTransactionId());
            dto.setStatus(order.getOkStatus());
            dto.setOrderNo(order.getTransactionId());
            dto.setApiCode(order.getProvider());
            dto.setGameType(order.getGameType());
            dto.setGameName(order.getGameName());
            dto.setBetTotal(betTotal);
            dto.setBetAmount(betAmount);
            dto.setProfitAmount(profitAmount);
            dto.setCreateTime(order.getCreateTime());
            dto.setGameName(StringUtils.isEmpty(gameName) ? order.getGameId() : gameName);
            dtos.add(dto);
        }
        return dtos;
    }


    public JSONObject getBettingRecords(String account, Integer page, Integer pageSize, String apiCode, String gameType, String gameName, String startTime, String endTime) {
        startTime = DateUtils.getDayStartTime(startTime);
        endTime = DateUtils.getDayEndTime(endTime);
        List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
        JSONObject json = new JSONObject();
        FutureTask<Integer> t1 = this.getBetSummary(json, account, apiCode, gameType, gameName, startTime, endTime);
        FutureTask<Integer> t2 = this.getBetOrders(json, account, page, pageSize, apiCode, gameType, gameName, startTime, endTime);
        tasks.add(t1);
        tasks.add(t2);
        ApiGateway.syncwait(tasks);
        return json;
    }

    protected FutureTask<Integer> getBetSummary(final JSONObject json, final String account, final String apiCode, final String gameType, final String gameName, final String startTime, final String endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Map<String, Object> map = bettingOrderDao.getBetSummary(account, apiCode, gameType, gameName, startTime, endTime);
                if (CollectionUtils.isEmpty(map)) {
                    return 0;
                }
                synchronized (json) {
                    json.putAll(map);
                }
                return 1;
            }
        });
        return futureTask;
    }

    protected FutureTask<Integer> getBetOrders(final JSONObject json, final String account, final Integer page, final Integer pageSize, final String apiCode, final String gameType, final String gameName, final String startTime, final String endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                PageData pageData = getOrders(account, page, pageSize, apiCode, gameType, gameName, startTime, endTime);
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

    public JSONObject getDailyRecords(String account, Integer page, Integer pageSize, String startTime, String endTime) {
        startTime = StringUtils.isEmpty(startTime) ? DateUtils.getBeforeThirtyDaysTime() : DateUtils.getDayStartTime(startTime);
        endTime = StringUtils.isEmpty(endTime) ? "" : DateUtils.getDayEndTime(endTime);
        List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
        JSONObject json = new JSONObject();
        FutureTask<Integer> t1 = this.getDailySummary(json, account, startTime, endTime);
        FutureTask<Integer> t2 = this.getDailyReport(json, account, page, pageSize, startTime, endTime);
        tasks.add(t1);
        tasks.add(t2);
        ApiGateway.syncwait(tasks);
        return json;
    }

    protected FutureTask<Integer> getDailySummary(final JSONObject json, final String account, final String startTime, final String endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Map<String, Object> map = bettingOrderDao.getBetSummary(account, "", "", "", startTime, endTime);
                if (CollectionUtils.isEmpty(map)) {
                    return 0;
                }
                synchronized (json) {
                    json.putAll(map);
                }
                return 1;
            }
        });
        return futureTask;
    }

    protected FutureTask<Integer> getDailyReport(final JSONObject json, final String account, final Integer page, final Integer pageSize, final String startTime, final String endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                PageData pageData = getDailyReport(account, page, pageSize, startTime, endTime);
                if (null == pageData) {
                    return 0;
                }
                synchronized (json) {
                    JSONObject obj = JSONObject.fromObject(pageData);
                    json.putAll(obj);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public PageData getDailyReport(String account, Integer page, Integer pageSize, String startTime, String endTime) {
        List<String> list = DateUtils.getSectionDate(startTime, endTime);
        Collections.reverse(list);
        PageData pageData = PageData.startPage(list, 0, page, pageSize);
        List<String> dates = (List<String>) pageData.getContents();
        List<DailyReportDto> dtos = this.getDailyReportData(account, dates);
        pageData.setContents(dtos);
        return pageData;
    }

    protected List<DailyReportDto> getDailyReportData(String account, List<String> dates) {
        List<DailyReportDto> dtos = new ArrayList<DailyReportDto>();
        if (CollectionUtils.isEmpty(dates)) {
            return dtos;
        }
        DailyReportDto dto = null;
        BetSummaryDto betSummary = null;
        Iterator<String> iterator = dates.iterator();
        while (iterator.hasNext()) {
            dto = new DailyReportDto();
            String date = iterator.next();
            String startTime = DateUtils.getDayStartTime(date);
            String endTime = DateUtils.getDayEndTime(date);
            Map<String, Object> map = bettingOrderDao.getBetSummary(account, "", "", "", startTime, endTime);
            betSummary = JsonUtil.map2Object(map, BetSummaryDto.class);
            dto.setCreateTime(date);
            dto.setBetTotal(betSummary.getBetTotalAmount());
            dto.setBetAmount(betSummary.getBetAmount());
            dto.setProfitAmount(betSummary.getProfitTotalAmount());
            dtos.add(dto);
        }
        return dtos;
    }
}
