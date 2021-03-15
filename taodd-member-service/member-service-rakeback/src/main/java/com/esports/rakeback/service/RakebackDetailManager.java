package com.esports.rakeback.service;


import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.esports.rakeback.bean.db1.RakebackCfg;
import com.esports.rakeback.bean.db1.RakebackDetail;
import com.esports.rakeback.bean.db1.RakebackOrder;
import com.esports.rakeback.dao.db1.RakebackDetailDao;
import com.esports.rakeback.dto.RakebackRecordDto;
import com.esports.rakeback.dto.WashSummaryDto;
import com.esports.utils.DateUtils;
import com.esports.utils.IPUtils;
import com.esports.utils.JsonUtil;
import com.esports.utils.PageData;
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

@Service
public class RakebackDetailManager {

    @Autowired
    private RakebackDetailDao rakebackDetailDao;

    @Autowired
    private RakebackCfgManager rakebackCfgManager;

    public WashSummaryDto geWashSummary(String account, String apiCode, String gameType, String startTime, String endTime) {
        WashSummaryDto dto = null;
        Map<String, Object> map = rakebackDetailDao.getWashSummary(account, apiCode, gameType, startTime, endTime);
        if (!CollectionUtils.isEmpty(map)) {
            dto = JsonUtil.map2Object(map, WashSummaryDto.class);
        }
        return dto;
    }

    public PageData getOrders(String account, Integer page, Integer pageSize, String apiCode, String gameType, String startTime, String endTime) {
        Sort.Order[] orders = {Sort.Order.desc("id"), Sort.Order.desc("createTime")};
        Sort sort = Sort.by(Arrays.asList(orders));
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Specification<RakebackDetail> spec = new Specification<RakebackDetail>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<RakebackDetail> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 增加筛选条件
                Predicate predicate = cb.conjunction();
                predicate.getExpressions().add(cb.gt(root.get("point").as(BigDecimal.class), BigDecimal.ZERO));
                if (!StringUtils.isEmpty(account)) {
                    predicate.getExpressions().add(cb.equal(root.get("account").as(String.class), account));
                }
                if (!StringUtils.isEmpty(apiCode)) {
                    predicate.getExpressions().add(cb.equal(root.get("platform").as(String.class), apiCode));
                }
                if (!StringUtils.isEmpty(gameType)) {
                    predicate.getExpressions().add(cb.equal(root.get("gameType").as(String.class), gameType));
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
        Page<RakebackDetail> pages = rakebackDetailDao.findAll(spec, pageable);
        PageData pageData = PageData.builder(pages);
        List<RakebackRecordDto> dtos = this.assembleWashData((List<RakebackDetail>) pageData.getContents());
        pageData.setContents(dtos);
        return pageData;
    }

    private List<RakebackRecordDto> assembleWashData(List<RakebackDetail> list) {
        List<RakebackRecordDto> dtos = new ArrayList<RakebackRecordDto>();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        RakebackRecordDto dto = null;
        Iterator<RakebackDetail> iterator = list.iterator();
        while (iterator.hasNext()) {
            RakebackDetail order = iterator.next();
            dto = new RakebackRecordDto();
            dto.setApiCode(order.getPlatform());
            dto.setGameType(order.getGameType());
            dto.setOrderNo(order.getOrderNo());
            dto.setCreateTime(order.getCreateTime());
            BigDecimal percentage = null != order.getPoint() ? order.getPoint() : BigDecimal.ZERO;
            BigDecimal betAmount = null != order.getBetAmount() ? order.getBetAmount() : BigDecimal.ZERO;
            BigDecimal rakeAmount = betAmount.multiply(percentage.divide(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_DOWN);
            dto.setBetValidAmount(betAmount);
            dto.setPercentage(percentage);
            dto.setRakeAmount(rakeAmount);
            if (rakeAmount.compareTo(BigDecimal.ZERO) > 0) {
                dtos.add(dto);
            }
        }
        return dtos;
    }


    /**
     * 结算洗码明细
     */
    @LcnTransaction
    public RakebackOrder settlementMemberRakeDetail(String account, String serialNo, String orderNo, String ip) {
        RakebackOrder rake = new RakebackOrder();
        Timestamp createTime = new Timestamp(System.currentTimeMillis());
        List<Map<String, Object>> list = rakebackDetailDao.getMemberRakeDetail(account);
        if (CollectionUtils.isEmpty(list)) {
            return rake;
        }
        Iterator<Map<String, Object>> iterator = list.iterator();
        BigDecimal betTotalValidAmount = BigDecimal.ZERO;
        BigDecimal rakeTotalAmount = BigDecimal.ZERO;
        BigDecimal winTotalAmount = BigDecimal.ZERO;
        Integer betTotalNum = 0;
        while (iterator.hasNext()) {
            Map<String, Object> map = iterator.next();
            //根据反水方案，处理各个游戏类反水
            RakebackDetail rakebackDetail = this.saveRakeDetail(account, serialNo, orderNo, map, ip);
            Integer betNum = null != rakebackDetail && null != rakebackDetail.getBetNum() ? rakebackDetail.getBetNum() : 0;
            BigDecimal betAmount = null != rakebackDetail && null != rakebackDetail.getBetAmount() ? rakebackDetail.getBetAmount() : BigDecimal.ZERO;
            BigDecimal profitAmount = null != rakebackDetail && null != rakebackDetail.getProfitAmount() ? rakebackDetail.getProfitAmount() : BigDecimal.ZERO;
            BigDecimal percentage = null != rakebackDetail && null != rakebackDetail.getPoint() ? rakebackDetail.getPoint() : BigDecimal.ZERO;
            BigDecimal rakeAmount = betAmount.multiply(percentage.divide(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_DOWN);
            rakeTotalAmount = rakeTotalAmount.add(rakeAmount);
            betTotalValidAmount = betTotalValidAmount.add(betAmount);
            winTotalAmount = winTotalAmount.add(profitAmount);
            betTotalNum = betTotalNum + betNum;
        }
        rake.setAccount(account);
        rake.setCreateTime(createTime);
        rake.setBetNum(betTotalNum);
        rake.setProfitAmount(winTotalAmount);
        rake.setBetValidAmount(betTotalValidAmount);
        rake.setRakeAmount(rakeTotalAmount);
        rake.setActualAmount(rakeTotalAmount);
        rake.setIp(IPUtils.ipToLong(ip));
        return rake;
    }

    @LcnTransaction
    public RakebackDetail saveRakeDetail(String account, String serialNo, String orderNo, Map<String, Object> map, String ip) {
        RakebackDetail rakebackDetail = JsonUtil.map2Object(map, RakebackDetail.class);
        String platform = null != rakebackDetail ? rakebackDetail.getPlatform() : "";
        String gameType = null != rakebackDetail ? rakebackDetail.getGameType() : "";
        RakebackCfg cfg = rakebackCfgManager.getRakebackCfg(account, platform, gameType);
        BigDecimal percentage = null != cfg && null != cfg.getPoint() && BigDecimal.ZERO.compareTo(cfg.getPoint()) < 0 ? cfg.getPoint() : BigDecimal.ZERO;
        Timestamp createTime = new Timestamp(System.currentTimeMillis());
        rakebackDetail.setId(null);
        rakebackDetail.setAccount(account);
        rakebackDetail.setSerialNo(serialNo);
        rakebackDetail.setOrderNo(orderNo);
        rakebackDetail.setPoint(percentage);
        rakebackDetail.setIp(IPUtils.ipToLong(ip));
        rakebackDetail.setCreateTime(createTime);
        rakebackDetailDao.save(rakebackDetail);
        return rakebackDetail;
    }
}