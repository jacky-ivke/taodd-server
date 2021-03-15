package com.esports.order.service;

import com.esports.api.log.AgentLogService;
import com.esports.constant.CapitalCode;
import com.esports.constant.PlayerCode;
import com.esports.constant.WalletCode;
import com.esports.order.bean.db1.DrawOrder;
import com.esports.order.dao.db1.DrawOrderDao;
import com.esports.order.dto.DrawRecordDto;
import com.esports.utils.DateUtils;
import com.esports.utils.IPUtils;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Service
public class DrawOrderManager {

    @Autowired
    private DrawOrderDao drawOrderDao;

    @Autowired
    private AgentLogService agentLogService;

    public BigDecimal getMemberDrawAmount(String account){
        BigDecimal amount = drawOrderDao.getMemberDrawAmount(account);
        return (null == amount ? BigDecimal.ZERO : amount);
    }

    public BigDecimal getAgentPendingDrawAmount(String account) {
        BigDecimal amount = drawOrderDao.getAgentPendingDrawAmount(account);
        return (null == amount ? BigDecimal.ZERO : amount);
    }

    public String saveDrawOrder(String walletType, Integer okStatus, Integer auditStatus, BigDecimal costAmount, Integer source, String account, BigDecimal amount, BigDecimal balance,
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
        order.setIdentity(PlayerCode._AGENT.getCode());
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
        agentLogService.saveTradeLog(account, walletType, type, secondType, okStatus, amount, orderNo, balance, ip, remarks);
        return orderNo;
    }

    public PageData getOrders(String account, Integer okStatus, Integer type, Integer page, Integer pageSize, String startTime, String endTime) {
        String identity = PlayerCode._AGENT.getCode();
        String wallet = WalletCode._BALANCE.getCode();
        Sort.Order[] orders = {Sort.Order.desc("id"), Sort.Order.desc("createTime")};
        Sort sort = Sort.by(Arrays.asList(orders));
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Specification<DrawOrder> spec = new Specification<DrawOrder>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<DrawOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 增加筛选条件
                Predicate predicate = cb.conjunction();
                if (!StringUtils.isEmpty(account)) {
                    predicate.getExpressions().add(cb.equal(root.get("account").as(String.class), account));
                }
                if (null != okStatus) {
                    predicate.getExpressions().add(cb.equal(root.get("okStatus").as(Integer.class), okStatus));
                }
                //0-代表提款到银行卡， 1-取款到中心钱包
                if (null != type && 0 == type) {
                    predicate.getExpressions().add(cb.notEqual(root.get("bankAccount").as(String.class), wallet));
                }else if(null != type && 1== type){
                    predicate.getExpressions().add(cb.equal(root.get("bankAccount").as(String.class), wallet));
                }
                if (!StringUtils.isEmpty(identity)) {
                    predicate.getExpressions().add(cb.equal(root.get("identity").as(String.class), identity));
                }
                if (!StringUtils.isEmpty(startTime)) {
                    Timestamp start = DateUtils.getDayStartTime(DateUtils.stringFormatDate(startTime));
                    predicate.getExpressions()
                            .add(cb.greaterThanOrEqualTo(root.get("createTime").as(Timestamp.class), start));
                }
                if (!StringUtils.isEmpty(endTime)) {
                    Timestamp end = DateUtils.getDayEndTime(DateUtils.stringFormatDate(endTime));
                    predicate.getExpressions()
                            .add(cb.lessThanOrEqualTo(root.get("createTime").as(Timestamp.class), end));
                }
                return predicate;
            }
        };
        Page<DrawOrder> pages = drawOrderDao.findAll(spec, pageable);
        PageData pageData = PageData.builder(pages);
        List<DrawRecordDto> dtos = this.assembleData((List<DrawOrder>) pageData.getContents());
        pageData.setContents(dtos);
        return pageData;
    }

    public List<DrawRecordDto> assembleData(List<DrawOrder> drawOrders) {
        List<DrawRecordDto> dtos = new ArrayList<DrawRecordDto>();
        if (CollectionUtils.isEmpty(drawOrders)) {
            return dtos;
        }
        DrawRecordDto dto = null;
        Iterator<DrawOrder> itrator = drawOrders.iterator();
        while (itrator.hasNext()) {
            DrawOrder order = itrator.next();
            dto = new DrawRecordDto();
            dto.setOrderNo(order.getOrderNo());
            dto.setOkStatus(order.getOkStatus());
            dto.setCreateTime(order.getCreateTime());
            dto.setBankAccount(order.getBankAccount());
            dto.setAmount(order.getAmount());
            dtos.add(dto);
        }
        return dtos;
    }
}
