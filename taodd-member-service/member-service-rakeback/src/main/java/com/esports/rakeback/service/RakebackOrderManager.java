package com.esports.rakeback.service;


import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.esports.api.center.MemberService;
import com.esports.api.order.MemberOrderService;
import com.esports.constant.CapitalCode;
import com.esports.constant.OrderCode;
import com.esports.rakeback.bean.db1.RakebackCfg;
import com.esports.rakeback.bean.db1.RakebackOrder;
import com.esports.rakeback.dao.db1.RakebackOrderDao;
import com.esports.rakeback.dto.RakebackDto;
import com.esports.utils.DateUtils;
import com.esports.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class RakebackOrderManager {

    @Autowired
    private RakebackOrderDao rakebackOrderDao;

    @Autowired
    private RakebackCfgManager rakebackCfgManager;

    @Autowired
    private RakebackDetailManager rakebackDetailManager;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberOrderService memberOrderService;

    private static final String RAKEBACK_SETTLEMENT = "【返水结算】操作者：%s";

    public BigDecimal getThisWeekRakeAmount(String account, String platform, String gameType) {
        BigDecimal rakeAmount = BigDecimal.ZERO;
        Timestamp startTime = DateUtils.getBeginDayOfWeek();
        Timestamp endTime = DateUtils.getEndDayOfWeek();
        rakeAmount = rakebackOrderDao.getSectionRakeAmount(account, startTime, endTime, platform, gameType);
        return rakeAmount;
    }

    public List<RakebackDto> getRakebackDetail(String account, String gameType) {
        List<RakebackDto> dtos = new ArrayList<RakebackDto>();
        List<RakebackCfg> list = this.getRakebackCfg(account);
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        Iterator<RakebackCfg> iterator = list.iterator();
        RakebackDto dto = null;
        while (iterator.hasNext()) {
            RakebackCfg cfg = iterator.next();
            if (!StringUtils.isEmpty(gameType) && !cfg.getGameType().equalsIgnoreCase(gameType)) {
                continue;
            }
            dto = this.getRakebackDetail(account, cfg);
            dtos.add(dto);
        }
        return dtos;
    }

    private RakebackDto getRakebackDetail(String account, RakebackCfg cfg) {
        RakebackDto dto = new RakebackDto();
        String platform = cfg.getPlatform();
        String gameType = cfg.getGameType();
        BigDecimal percentage = null != cfg.getPoint() ? cfg.getPoint() : BigDecimal.ZERO;
        BigDecimal weekBetAmount = this.getThisWeekRakeAmount(account, platform, gameType);
        BigDecimal betValidAmount = rakebackOrderDao.getCurrentSettlementWashing(account, platform, gameType);
        BigDecimal rakeAmount = betValidAmount.multiply(percentage.divide(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_DOWN);
        dto.setApiCode(platform);
        dto.setGameType(gameType);
        dto.setPercentage(percentage);
        dto.setWeekBetAmount(weekBetAmount);
        dto.setBetValidAmount(betValidAmount);
        dto.setRakeAmount(rakeAmount);
        return dto;
    }

    private List<RakebackCfg> getRakebackCfg(String account) {
        String rakeSchemeCode = memberService.getRakeSchemeCode(account);
        Integer vip = memberService.getVip(account);
        List<RakebackCfg> list = rakebackCfgManager.getRakebackCfg(rakeSchemeCode, vip);
        return list;
    }

    /**
     * 获取本期返水总额
     */
    public BigDecimal getRakebackTotalAmount(String account) {
        BigDecimal totalWashingSettle = BigDecimal.ZERO;
        List<RakebackCfg> list = this.getRakebackCfg(account);
        if (CollectionUtils.isEmpty(list)) {
            return totalWashingSettle;
        }
        Iterator<RakebackCfg> iterator = list.iterator();
        while (iterator.hasNext()) {
            RakebackCfg cfg = iterator.next();
            String platform = cfg.getPlatform();
            String gameType = cfg.getGameType();
            BigDecimal percentage = cfg.getPoint();
            BigDecimal betValidAmount = rakebackOrderDao.getCurrentSettlementWashing(account, platform, gameType);
            BigDecimal rakeAmount = betValidAmount.multiply(percentage.divide(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_DOWN);
            totalWashingSettle = totalWashingSettle.add(rakeAmount);
        }
        return totalWashingSettle;
    }

    public String createSerialNo() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String serialNo = sdf.format(date);
        return serialNo;
    }

    @LcnTransaction
    public BigDecimal saveMemberRake(String account, String ip) {
        String serialNo = this.createSerialNo();
        String orderNo = RandomUtil.getUUID("");
        Integer okStatus = OrderCode._SUCCESS.getCode();
        String remarks = String.format(RAKEBACK_SETTLEMENT, account);
        String type = CapitalCode._AWARD_RAKEBACK.getCode();
        String pos = CapitalCode._AWARD_RAKEBACK.getMessage();
        //结算各个API反水
        RakebackOrder order = rakebackDetailManager.settlementMemberRakeDetail(account, serialNo, orderNo, ip);
        BigDecimal amount = order.getRakeAmount();
        order.setId(null);
        order.setOrderNo(orderNo);
        order.setOkStatus(okStatus);
        order.setRemarks(remarks);
        order.setSerialNo(serialNo);
        order.setActualAmount(amount);
        rakebackOrderDao.save(order);
        BigDecimal balance = memberService.updateBalance(account, amount);
        memberOrderService.saveActivityOrder(okStatus, type, amount, balance, account, remarks, Boolean.FALSE, ip, pos);
        return balance;
    }
}
