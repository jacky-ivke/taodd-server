package com.esports.report.service;

import com.esports.processor.ApiGateway;
import com.esports.report.dao.db1.AgentReportDao;
import com.esports.report.dto.*;
import com.esports.utils.DateUtils;
import com.esports.utils.JsonUtil;
import com.esports.utils.PageData;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Service
public class AgentReportManager {

    @Autowired
    private AgentReportDao agentReportDao;

    public MonthDataDto getCurrentMonthData(String account) {
        MonthDataDto dto = new MonthDataDto();
        List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
        FutureTask<Integer> t1 = this.getCurrMonthRegSubMember(dto, account);
        FutureTask<Integer> t2 = this.getCurrMonthSubMemberFirstDeposit(dto, account);
        FutureTask<Integer> t3 = this.getCurrMonthActvieSubMember(dto, account);
        FutureTask<Integer> t4 = this.getCurrMonthSubMemberNetWin(dto, account);
        tasks.add(t1);
        tasks.add(t2);
        tasks.add(t3);
        tasks.add(t4);
        ApiGateway.syncwait(tasks);
        return dto;
    }

    public PageData getSubMemberBet(String leader, String member, Integer page, Integer pageSize, String apiCode, String startTime, String endTime) {
        startTime = StringUtils.isEmpty(startTime) ? DateUtils.getBeforeThirtyDaysTime() : startTime;
        endTime = StringUtils.isEmpty(endTime) ? DateUtils.getLastDayOfCurrentMonth() : endTime;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Map<String, Object>> pages = agentReportDao.findAll(leader, member, apiCode, startTime, endTime, pageable);
        PageData pageData = PageData.builder(pages);
        List<SubMemberBetDto> dtos = this.assembleSubMemberBetData((List<Map<String, Object>>) pageData.getContents());
        pageData.setContents(dtos);
        return pageData;
    }

    private List<SubMemberBetDto> assembleSubMemberBetData(List<Map<String, Object>> betOrders) {
        List<SubMemberBetDto> dtos = new ArrayList<SubMemberBetDto>();
        if (CollectionUtils.isEmpty(betOrders)) {
            return dtos;
        }
        SubMemberBetDto dto = null;
        Iterator<Map<String, Object>> itrator = betOrders.iterator();
        while (itrator.hasNext()) {
            Map<String, Object> map = itrator.next();
            if (CollectionUtils.isEmpty(map)) {
                continue;
            }
            dto = JsonUtil.map2Object(map, SubMemberBetDto.class);
            dtos.add(dto);
        }
        return dtos;
    }

    public FutureTask<Integer> getCurrMonthSubMemberNetWin(final MonthDataDto dto, final String account) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                BigDecimal profitAmount = agentReportDao.getCurrMonthSubMemberBetWinAmount(account);
                BigDecimal discountAmount = agentReportDao.getCurrMonthSubMemberAwardAmount(account);
                BigDecimal netWinAmount = profitAmount.add(discountAmount);
                synchronized (dto) {
                    dto.setNetWinAmount(netWinAmount);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getCurrMonthRegSubMember(final MonthDataDto dto, final String account) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Integer registerMember = agentReportDao.getCurrMonthRegSubMember(account);
                synchronized (dto) {
                    dto.setRegisterMember(null == registerMember ? 0 : registerMember);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getCurrMonthSubMemberFirstDeposit(final MonthDataDto dto, final String account) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Integer registerFirstMember = agentReportDao.getCurrMonthSubMemberFirstDeposit(account);
                synchronized (dto) {
                    dto.setFirstDepositMember(null == registerFirstMember ? 0 : registerFirstMember);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getCurrMonthActvieSubMember(final MonthDataDto dto, final String account) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Integer activeMember = agentReportDao.getCurrMonthActvieSubMember(account);
                synchronized (dto) {
                    dto.setActiveMember(null == activeMember ? 0 : activeMember);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FinanceReportDto getFinanceReport(String account, String startTime, String endTime) {
        FinanceReportDto dto = new FinanceReportDto();
        List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
        startTime = StringUtils.isEmpty(startTime) ? DateUtils.getFirstDayOfCurrentMonth() : startTime;
        endTime = StringUtils.isEmpty(endTime) ? DateUtils.getLastDayOfCurrentMonth() : endTime;
        Timestamp start = DateUtils.getDayStartTime(DateUtils.stringFormatDate(startTime));
        Timestamp end = DateUtils.getDayEndTime(DateUtils.stringFormatDate(endTime));
        FutureTask<Integer> t1 = this.getSubMemberDepositAmount(dto, account, start, end);
        FutureTask<Integer> t2 = this.getSubMemberDrawAmount(dto, account, start, end);
        FutureTask<Integer> t3 = this.getSubMemberAwardAmount(dto, account, start, end);
//        FutureTask<Integer> t4 = this.getSubMemberRakeAmount(dto, account, start, end);
        FutureTask<Integer> t5 = this.getSubMemberBetWin(dto, account, start, end);
        FutureTask<Integer> t6 = this.getAgentPlatformFee(dto, account, start, end);
        tasks.add(t1);
        tasks.add(t2);
        tasks.add(t3);
//        tasks.add(t4);
        tasks.add(t5);
        tasks.add(t6);
        ApiGateway.syncwait(tasks);
        BigDecimal winTotalAmount = dto.getWinTotalAmount();
        BigDecimal discountAmount = dto.getDiscountAmount();
        BigDecimal netWinAmount = winTotalAmount.add(discountAmount);
        return dto;
    }

    public FutureTask<Integer> getSubMemberDepositAmount(final FinanceReportDto dto, final String account, final Timestamp startTime, final Timestamp endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                BigDecimal depositAmount = agentReportDao.getSubMemberDepositAmount(account, startTime, endTime);
                synchronized (dto) {
                    dto.setDepositAmount(depositAmount);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getSubMemberDrawAmount(final FinanceReportDto dto, final String account, final Timestamp startTime, final Timestamp endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                BigDecimal drawAmount = agentReportDao.getSubMemberDrawAmount(account, startTime, endTime);
                synchronized (dto) {
                    dto.setDrawAmount(drawAmount);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getSubMemberAwardAmount(final FinanceReportDto dto, final String account, final Timestamp startTime, final Timestamp endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                BigDecimal discountAmount = agentReportDao.getSubMemberAwardAmount(account, startTime, endTime);
                synchronized (dto) {
                    dto.setDiscountAmount(discountAmount);
                    return 1;
                }
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getSubMemberRakeAmount(final FinanceReportDto dto, final String account,
                                                      final Timestamp startTime, final Timestamp endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                BigDecimal rakeAmount = agentReportDao.getSubMemberRakeAmount(account, startTime, endTime);
                synchronized (dto) {
                    dto.setRakeAmount(rakeAmount);
                    return 1;
                }
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getSubMemberBetWin(final FinanceReportDto dto, final String account, final Timestamp startTime, final Timestamp endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                BigDecimal winTotalAmount = agentReportDao.getSubMemberBetWin(account, startTime, endTime);
                synchronized (dto) {
                    dto.setWinTotalAmount(winTotalAmount);
                    return 1;
                }
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getAgentPlatformFee(final FinanceReportDto dto, final String account, final Timestamp startTime, final Timestamp endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                BigDecimal platformFee = agentReportDao.getAgentPlatformFee(account, startTime, endTime);
                dto.setPlatformFee(platformFee);
                return 1;
            }
        });
        return futureTask;
    }

    public CommissionReportDto getCommissionReport(String account, String month) {
        CommissionReportDto dto = new CommissionReportDto();
        List<Map<String, Object>> list = agentReportDao.getAgentCommission(account, month);
        if (CollectionUtils.isEmpty(list)) {
            return dto;
        }
        BigDecimal profitAmountTotal = BigDecimal.ZERO;
        BigDecimal commissionAmountTotal = BigDecimal.ZERO;
        BigDecimal platformFeeTotal = BigDecimal.ZERO;
        Integer activeMemberTotal = agentReportDao.getCommissionActiveMember(account, month);
        Iterator<Map<String, Object>> it = list.iterator();
        while (it.hasNext()) {
            Map<String, Object> map = it.next();
            if (CollectionUtils.isEmpty(map)) {
                continue;
            }
            CommissionReportDto reportDto = JsonUtil.map2Object(map, CommissionReportDto.class);
            BigDecimal profitAmount = null == reportDto.getProfitAmount() ? BigDecimal.ZERO : reportDto.getProfitAmount();
            BigDecimal commissionAmount = null == reportDto.getCommissionAmount() ? BigDecimal.ZERO : reportDto.getCommissionAmount();
            BigDecimal platformFee = null == reportDto.getPlatformFee() ? BigDecimal.ZERO : reportDto.getPlatformFee();
            profitAmountTotal = profitAmountTotal.add(profitAmount);
            commissionAmountTotal = commissionAmountTotal.add(commissionAmount);
            platformFeeTotal = platformFeeTotal.add(platformFee);
        }
        dto.setCommissionAmount(commissionAmountTotal);
        dto.setProfitAmount(profitAmountTotal);
        dto.setActiveMember(activeMemberTotal);
        dto.setPlatformFee(platformFeeTotal);
        return dto;
    }

    public PlatformDataDto getPlatformReport(String account) {
        PlatformDataDto dto = new PlatformDataDto();
        List<PlatformReportDto> dtos = new ArrayList<PlatformReportDto>();
        List<Map<String, Object>> list = agentReportDao.getAgentPlatformProfit(account);
        if (CollectionUtils.isEmpty(list)) {
            dto.setContents(dtos);
            return dto;
        }
        Iterator<Map<String, Object>> it = list.iterator();
        PlatformReportDto reportDto = null;
        BigDecimal profitTotalAmount = BigDecimal.ZERO;
        BigDecimal betTotalAmount = BigDecimal.ZERO;
        BigDecimal platformTotalAmount = BigDecimal.ZERO;
        BigDecimal activityTotalAmount = agentReportDao.getCommissionSubMemberAtyAmount(account);
        while (it.hasNext()) {
            Map<String, Object> map = it.next();
            reportDto = JsonUtil.map2Object(map, PlatformReportDto.class);
            BigDecimal profitAmount = null == reportDto.getProfitAmount() ? BigDecimal.ZERO : reportDto.getProfitAmount();
            BigDecimal betTotal = null == reportDto.getBetAmount() ? BigDecimal.ZERO : reportDto.getBetAmount();
            BigDecimal platformFee = null == reportDto.getPlatformFee() ? BigDecimal.ZERO : reportDto.getPlatformFee();
            profitTotalAmount = profitTotalAmount.add(profitAmount);
            betTotalAmount = betTotalAmount.add(betTotal);
            platformTotalAmount = platformTotalAmount.add(platformFee);
            dtos.add(reportDto);
        }
        dto.setProfitTotalAmount(profitTotalAmount);
        dto.setActivityTotalAmount(activityTotalAmount);
        dto.setPlatformTotalAmount(platformTotalAmount);
        dto.setBetTotalAmount(betTotalAmount);
        dto.setContents(dtos);
        return dto;
    }

    public CommissionDetailDto getCommissionDetail(String account, String month) {
        CommissionDetailDto dto = new CommissionDetailDto();
        List<Map<String, Object>> list = agentReportDao.getAgentCommission(account, month);
        if (CollectionUtils.isEmpty(list)) {
            return dto;
        }
        Iterator<Map<String, Object>> it = list.iterator();
        BigDecimal apiTakeAmount = BigDecimal.ZERO;
        BigDecimal profitAmount = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal rakeAmount = BigDecimal.ZERO;
        BigDecimal depositAmount = BigDecimal.ZERO;
        BigDecimal drawAmount = BigDecimal.ZERO;
        BigDecimal commissionAmount = BigDecimal.ZERO;
        BigDecimal actualAmount = BigDecimal.ZERO;
        BigDecimal unsettledAmount = BigDecimal.ZERO;
        Timestamp lastCreateTime = null;
        while (it.hasNext()) {
            Map<String, Object> map = it.next();
            CommissionOrderDto order = JsonUtil.map2Object(map, CommissionOrderDto.class);
            apiTakeAmount = apiTakeAmount.add(null == order.getApiTakeAmount() ? BigDecimal.ZERO : order.getApiTakeAmount());
            profitAmount = profitAmount.add(null == order.getProfitAmount() ? BigDecimal.ZERO : order.getProfitAmount());
            discountAmount = discountAmount.add(null == order.getDiscountAmount() ? BigDecimal.ZERO : order.getDiscountAmount());
            rakeAmount = rakeAmount.add(null == order.getRakebacAmount() ? BigDecimal.ZERO : order.getRakebacAmount());
            depositAmount = depositAmount.add(null == order.getDepositAmount() ? BigDecimal.ZERO : order.getDepositAmount());
            drawAmount = drawAmount.add(null == order.getDrawAmount() ? BigDecimal.ZERO : order.getDrawAmount());
            commissionAmount = commissionAmount.add(null == order.getCommissionAmount() ? BigDecimal.ZERO : order.getCommissionAmount());
            actualAmount = actualAmount.add(null == order.getActualAmount() ? BigDecimal.ZERO : order.getActualAmount());
            unsettledAmount = unsettledAmount.add(null == order.getUnsettledAmount() ? BigDecimal.ZERO : order.getUnsettledAmount());
            lastCreateTime = order.getCreateTime();
        }
        dto.setCreateTime(lastCreateTime);
        dto.setApiTakeAmount(apiTakeAmount);
        dto.setProfitAmount(profitAmount);
        dto.setDiscountAmount(discountAmount);
        dto.setRakeAmount(rakeAmount);
        dto.setDepositCharge(depositAmount);
        dto.setDrawCharge(drawAmount);
        dto.setCommissionAmount(commissionAmount);
        dto.setActualAmount(actualAmount);
        dto.setUnsettledAmount(unsettledAmount);
        return dto;
    }

    public JSONObject getDataCompareActvie(String account, String month) {
        JSONObject json = new JSONObject();
        month = StringUtils.isEmpty(month) ? DateUtils.getMonth() : month;
        List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
        FutureTask<Integer> t1 = this.getSubMemberRegister(json, account, month);
        FutureTask<Integer> t2 = this.getOldSubMemberDeposit(json, account, month);
        FutureTask<Integer> t3 = this.getSubMemberFirstDeposit(json, account, month);
        FutureTask<Integer> t4 = this.getSubMemberDeposit(json, account, month);
        FutureTask<Integer> t5 = this.getSubMemberDraw(json, account, month);
        FutureTask<Integer> t6 = this.getSubMemberBet(json, account, month);
        tasks.add(t1);
        tasks.add(t2);
        tasks.add(t3);
        tasks.add(t4);
        tasks.add(t5);
        tasks.add(t6);
        ApiGateway.syncwait(tasks);
        return json;
    }

    public JSONObject getDataCompareAmount(String account, String month) {
        JSONObject json = new JSONObject();
        month = StringUtils.isEmpty(month) ? DateUtils.getMonth() : month;
        List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
        FutureTask<Integer> t1 = this.getSubRegMemberDepositAmount(json, account, month);
        FutureTask<Integer> t2 = this.getOldSubMemberDepositAmount(json, account, month);
        FutureTask<Integer> t3 = this.getSubMemberFirstDepositAmount(json, account, month);
        FutureTask<Integer> t4 = this.getSubMemberDepositAmount(json, account, month);
        FutureTask<Integer> t5 = this.getSubMemberDrawAmount(json, account, month);
        FutureTask<Integer> t6 = this.getSubMemberBetAmount(json, account, month);
        FutureTask<Integer> t7 = this.getSubMemberBetProfitAmount(json, account, month);
        tasks.add(t1);
        tasks.add(t2);
        tasks.add(t3);
        tasks.add(t4);
        tasks.add(t5);
        tasks.add(t6);
        tasks.add(t7);
        ApiGateway.syncwait(tasks);
        return json;
    }

    public FutureTask<Integer> getSubMemberRegister(final JSONObject json, final String account, final String month) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Map<String, Object> map = agentReportDao.getSubMemberRegisterByDate(account, month);
                synchronized (json) {
                    Collection<Object> objs = null;
                    if (!CollectionUtils.isEmpty(map)) {
                        objs = map.values();
                    }
                    json.put("registerMember", objs);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getOldSubMemberDeposit(final JSONObject json, final String account, final String month) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Map<String, Object> map = agentReportDao.getOldSubMemberDeposit(account, month);
                synchronized (json) {
                    Collection<Object> objs = null;
                    if (!CollectionUtils.isEmpty(map)) {
                        objs = map.values();
                    }
                    json.put("oldDeposit", objs);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getSubMemberFirstDeposit(final JSONObject json, final String account, final String month) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Map<String, Object> map = agentReportDao.getSubMemberFirstDeposit(account, month);
                synchronized (json) {
                    Collection<Object> objs = null;
                    if (!CollectionUtils.isEmpty(map)) {
                        objs = map.values();
                    }
                    json.put("firstDeposit", objs);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getSubMemberDeposit(final JSONObject json, final String account, final String month) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Map<String, Object> map = agentReportDao.getSubMemberDeposit(account, month);
                synchronized (json) {
                    Collection<Object> objs = null;
                    if (!CollectionUtils.isEmpty(map)) {
                        objs = map.values();
                    }
                    json.put("deposit", objs);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getSubMemberDraw(final JSONObject json, final String account, final String month) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Map<String, Object> map = agentReportDao.getSubMemberDraw(account, month);
                synchronized (json) {
                    Collection<Object> objs = null;
                    if (!CollectionUtils.isEmpty(map)) {
                        objs = map.values();
                    }
                    json.put("draw", objs);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getSubMemberBet(final JSONObject json, final String account, final String month) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Map<String, Object> map = agentReportDao.getSubMemberBet(account, month);
                synchronized (json) {
                    Collection<Object> objs = null;
                    if (!CollectionUtils.isEmpty(map)) {
                        objs = map.values();
                    }
                    json.put("betting", objs);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getSubRegMemberDepositAmount(final JSONObject json, final String account, final String month) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Map<String, Object> map = agentReportDao.getSubRegMemberDepositAmount(account, month);
                synchronized (json) {
                    Collection<Object> objs = null;
                    if (!CollectionUtils.isEmpty(map)) {
                        objs = map.values();
                    }
                    json.put("registerDepositAmount", objs);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getOldSubMemberDepositAmount(final JSONObject json, final String account, final String month) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Map<String, Object> map = agentReportDao.getOldSubMemberDepositAmount(account, month);
                synchronized (json) {
                    Collection<Object> objs = null;
                    if (!CollectionUtils.isEmpty(map)) {
                        objs = map.values();
                    }
                    json.put("oldDepositAmount", objs);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getSubMemberFirstDepositAmount(final JSONObject json, final String account, final String month) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Map<String, Object> map = agentReportDao.getSubMemberFirstDepositAmount(account, month);
                synchronized (json) {
                    Collection<Object> objs = null;
                    if (!CollectionUtils.isEmpty(map)) {
                        objs = map.values();
                    }
                    json.put("firstDepositAmount", objs);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getSubMemberDepositAmount(final JSONObject json, final String account, final String month) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Map<String, Object> map = agentReportDao.getSubMemberDepositAmount(account, month);
                synchronized (json) {
                    Collection<Object> objs = null;
                    if (!CollectionUtils.isEmpty(map)) {
                        objs = map.values();
                    }
                    json.put("depositAmount", objs);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getSubMemberDrawAmount(final JSONObject json, final String account, final String month) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Map<String, Object> map = agentReportDao.getSubMemberDrawAmount(account, month);
                synchronized (json) {
                    Collection<Object> objs = null;
                    if (!CollectionUtils.isEmpty(map)) {
                        objs = map.values();
                    }
                    json.put("drawAmount", objs);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getSubMemberBetAmount(final JSONObject json, final String account, final String month) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Map<String, Object> map = agentReportDao.getSubMemberBetAmount(account, month);
                synchronized (json) {
                    Collection<Object> objs = null;
                    if (!CollectionUtils.isEmpty(map)) {
                        objs = map.values();
                    }
                    json.put("bettingAmount", objs);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getSubMemberBetProfitAmount(final JSONObject json, final String account,
                                                           final String month) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Map<String, Object> map = agentReportDao.getSubMemberBetProfitAmount(account, month);
                synchronized (json) {
                    Collection<Object> objs = null;
                    if (!CollectionUtils.isEmpty(map)) {
                        objs = map.values();
                    }
                    json.put("profitAmount", objs);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public List<CommissionSchemeDto> getCommissionScheme(String account, String month) {

        List<CommissionSchemeDto> dtos = this.getSettledScheme(account, month);
        if(CollectionUtils.isEmpty(dtos)){
            dtos = this.getCurrentScheme(account);
        }
        return dtos;
    }

    public List<CommissionSchemeDto> getSettledScheme(String account, String month) {
        List<CommissionSchemeDto> dtos = new ArrayList<CommissionSchemeDto>();
        List<Map<String, Object>> list = agentReportDao.getCommisionSettledScheme(account, month);
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        Iterator<Map<String, Object>> iterator = list.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> map = iterator.next();
            if (CollectionUtils.isEmpty(map)) {
                continue;
            }
            CommissionSchemeDto dto = JsonUtil.map2Object(map, CommissionSchemeDto.class);
            dtos.add(dto);
        }
        return dtos;
    }

    public List<CommissionSchemeDto> getCurrentScheme(String account) {
        List<CommissionSchemeDto> dtos = new ArrayList<CommissionSchemeDto>();
        Timestamp commissionUptoDate = DateUtils.getYesterDay();
        Map<String, Object> map = agentReportDao.getAgentTotalProfitAndVaildMember(account, commissionUptoDate);
        if(CollectionUtils.isEmpty(map)){
            return dtos;
        }
        // 获取代理的总盈亏和有效人数
        String commissionCode = agentReportDao.getAgentCommissionCode(account);
        AgentCommissionDto dto = JsonUtil.map2Object(map, AgentCommissionDto.class);
        BigDecimal totalProfitAmount = null != dto.getProfitTotalAmount() ? dto.getProfitTotalAmount() : BigDecimal.ZERO;
        Integer totalVaildMember = null != dto.getMemNum() ? dto.getMemNum() : 0;
        // 根据有效会员数和本期总盈亏获取返佣方案
        List<Map<String, Object>> list = agentReportDao.getCommissionSchemeCfg(commissionCode, totalProfitAmount, totalVaildMember);
        list = CollectionUtils.isEmpty(list)? agentReportDao.getDefaultCommissionSchemeCfg(commissionCode) : list;
        dtos = this.assembleSchemeCfgData(list);
        return dtos;
    }

    protected List<CommissionSchemeDto> assembleSchemeCfgData(List<Map<String,Object>> list) {
        List<CommissionSchemeDto> dtos = new ArrayList<CommissionSchemeDto>();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        Iterator<Map<String,Object>> iterator = list.iterator();
        CommissionSchemeDto dto = null;
        while (iterator.hasNext()) {
            Map<String,Object> map = iterator.next();
            if(CollectionUtils.isEmpty(map)){
                continue;
            }
            dto = JsonUtil.map2Object(map, CommissionSchemeDto.class);
            dtos.add(dto);
        }
        return dtos;
    }
}
