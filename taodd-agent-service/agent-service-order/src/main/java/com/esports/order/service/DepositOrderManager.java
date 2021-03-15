package com.esports.order.service;

import com.esports.api.log.AgentLogService;
import com.esports.constant.CapitalCode;
import com.esports.constant.PlayerCode;
import com.esports.order.bean.db1.DepositOrder;
import com.esports.order.dao.db1.DepositOrderDao;
import com.esports.order.dto.DepositRecordDto;
import com.esports.utils.DateUtils;
import com.esports.utils.IPUtils;
import com.esports.utils.PageData;
import com.esports.utils.RandomUtil;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Service
public class DepositOrderManager {

    @Autowired
    private DepositOrderDao depositOrderDao;

    @Autowired
    private AgentLogService agentLogService;

    public BigDecimal getMemberDepositAmount(String account) {
        BigDecimal amount = depositOrderDao.getMemberDepositAmount(account);
        return (null == amount ? BigDecimal.ZERO : amount);
    }

    public JSONObject saveDepositOrder(String walletType, String bankRealName, String bankAccount, String payType, String channelType, String channelName,
                                       Integer okStatus, Integer source, String account, BigDecimal amount, BigDecimal balance, String ip, String remarks) {
        JSONObject json = null;
        String orderNo = this.createOrders(walletType, bankAccount, payType, channelType, channelName, okStatus, source, account, amount, balance, ip, remarks);
        if (payType.indexOf("online") >= 0) {
            json = this.createOnlineDepositOrder(orderNo);
        } else {
            json = this.createCompanyDepositOrder(orderNo, amount, bankRealName, bankAccount, channelName);
        }
        return json;
    }

    protected String createOrders(String walletType, String bankAccount, String payType, String channelType, String channelName,
                                  Integer okStatus, Integer source, String account, BigDecimal amount, BigDecimal balance, String ip, String remarks) {
        //生成随机金额
        BigDecimal postscript = amount.add(new BigDecimal(RandomUtil.randomCommon()).divide(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_DOWN);
        DepositOrder order = new DepositOrder();
        String orderNo = RandomUtil.getUUID("");
        order.setIdentity(PlayerCode._AGENT.getCode());
        order.setOkStatus(okStatus);
        order.setOrderNo(orderNo);
        order.setAmount(amount);
        order.setAccount(account);
        order.setRandomAmount(postscript);
        order.setSource(source);
        order.setActualAmount(BigDecimal.ZERO);
        order.setChannelName(channelName);
        order.setBankAccount(bankAccount);
        order.setPayType(payType);
        order.setType(channelType);
        order.setCreateTime(new Timestamp(System.currentTimeMillis()));
        order.setIp(IPUtils.ipToLong(ip));
        order.setRemarks(remarks);
        depositOrderDao.save(order);
        String logType = CapitalCode._DEPOSIT.getCode();
        agentLogService.saveTradeLog(account, walletType, logType, payType, okStatus, amount, orderNo, balance, ip, remarks);
        return orderNo;
    }

    protected JSONObject createCompanyDepositOrder(String orderNo, BigDecimal amount, String bankRealName, String bankAccount, String channelName) {
        JSONObject json = new JSONObject();
        json.put("orderNo", orderNo);
        json.put("amount", amount);
        json.put("bankRealName", bankRealName);
        json.put("bankAccount", bankAccount);
        json.put("channelName", channelName);
        return json;
    }

    protected JSONObject createOnlineDepositOrder(String orderNo) {
        JSONObject json = new JSONObject();
        json.put("orderNo", orderNo);
        json.put("payUrl", "");
        return json;
    }

    public PageData getOrders(String account, String type, Integer okStatus, Integer page, Integer pageSize, String startTime, String endTime) {
        String identity = PlayerCode._AGENT.getCode();
        Sort.Order[] orders = {Sort.Order.desc("id"), Sort.Order.desc("createTime")};
        Sort sort = Sort.by(Arrays.asList(orders));
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Specification<DepositOrder> spec = new Specification<DepositOrder>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<DepositOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 增加筛选条件
                Predicate predicate = cb.conjunction();
                if (!StringUtils.isEmpty(account)) {
                    predicate.getExpressions().add(cb.equal(root.get("account").as(String.class), account));
                }
                if (!StringUtils.isEmpty(type)) {
                    predicate.getExpressions().add(cb.equal(root.get("payType").as(String.class), type));
                }
                if (null != okStatus) {
                    predicate.getExpressions().add(cb.equal(root.get("okStatus").as(Integer.class), okStatus));
                }
                if (!StringUtils.isEmpty(identity)) {
                    predicate.getExpressions().add(cb.equal(root.get("identity").as(String.class), identity));
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
        Page<DepositOrder> pages = depositOrderDao.findAll(spec, pageable);
        PageData pageData = PageData.builder(pages);
        List<DepositRecordDto> dtos = this.assembleData((List<DepositOrder>) pageData.getContents());
        pageData.setContents(dtos);
        return pageData;
    }

    public List<DepositRecordDto> assembleData(List<DepositOrder> depositOrders) {
        List<DepositRecordDto> dtos = new ArrayList<DepositRecordDto>();
        if (CollectionUtils.isEmpty(depositOrders)) {
            return dtos;
        }
        DepositRecordDto dto = null;
        Iterator<DepositOrder> itrator = depositOrders.iterator();
        while (itrator.hasNext()) {
            DepositOrder order = itrator.next();
            dto = new DepositRecordDto();
            dto.setOrderNo(order.getOrderNo());
            dto.setOkStatus(order.getOkStatus());
            dto.setType(order.getPayType());
            dto.setCreateTime(order.getCreateTime());
            dto.setAmount(order.getAmount());
            dtos.add(dto);
        }
        return dtos;
    }

}
