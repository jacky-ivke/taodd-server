package com.esports.transfer.service;

import com.esports.api.center.AgentService;
import com.esports.api.log.AgentLogService;
import com.esports.transfer.bean.db1.AgentTransferSubOrder;
import com.esports.constant.CapitalCode;
import com.esports.constant.OrderCode;
import com.esports.constant.WalletCode;
import com.esports.transfer.dao.db1.AgentTransferSubDao;
import com.esports.transfer.dto.TransferSubMemberDto;
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
public class AgentTransferSubManager {

    @Autowired
    private AgentTransferSubDao agentTransferSubDao;

    @Autowired
    private AgentService agentService;

    @Autowired
    private AgentLogService agentLogService;

    public boolean checkTradePwd(String account, String password) {
        boolean success = agentService.checkTradePwd(account, password);
        return success;
    }

    public boolean checkSubMember(String leader, String member) {
        boolean success = agentService.checkSubMember(leader, member);
        return success;
    }

    public boolean checkBalance(String type, String account, BigDecimal amount) {
        if (WalletCode._COMMISSION.getCode().equals(type)) {
            return agentService.checkCommissionBalance(account, amount);
        }
        return agentService.checkOtherBalance(account, amount);
    }

    public String createTransferOrder(Integer source, String agent, String member, String walletType, BigDecimal amount, String remarks, String ip) {
        Integer okStatus = OrderCode._SUCCESS.getCode();
        //保存代存记录
        String orderNo = this.saveTransferOrder(source, agent, member, walletType, amount, remarks, ip);
        //代理账号扣除为下线代存的金额
        this.updateAgentBalance(okStatus, walletType, orderNo, agent, amount, ip, remarks);
        //下线会员账户余额增加代存金额
        this.updateSubMemberBalance(okStatus, orderNo, member, agent, amount, ip, remarks);
        return orderNo;
    }

    public String saveTransferOrder(Integer source, String agent, String member, String walletType, BigDecimal amount, String remarks, String ip) {
        Integer okStatus = OrderCode._SUCCESS.getCode();
        String orderNo = RandomUtil.getUUID("");
        AgentTransferSubOrder order = new AgentTransferSubOrder();
        Timestamp createTime = new Timestamp(System.currentTimeMillis());
        order.setOrderNo(orderNo);
        order.setOkStatus(okStatus);
        order.setAgent(agent);
        order.setMember(member);
        order.setAmount(amount);
        order.setSource(source);
        order.setWalletType(walletType);
        order.setRemarks(remarks);
        order.setCreateTime(createTime);
        order.setIp(IPUtils.ipToLong(ip));
        agentTransferSubDao.save(order);
        return orderNo;
    }

    public void updateAgentBalance(Integer okStatus, String walletType, String orderNo, String account, BigDecimal amount, String ip, String remarks) {
        String type = CapitalCode._TRASNFER.getCode();
        String secondType = CapitalCode._TRANSFER_MEMBER.getCode();
        BigDecimal balance = this.updateWalletBalance(account, walletType, amount);
        agentLogService.saveTradeLog(account, walletType, type, secondType, okStatus, amount, orderNo, balance, ip, remarks);
    }

    public void updateSubMemberBalance(Integer okStatus, String orderNo, String account, String agent, BigDecimal amount, String ip, String remarks) {
        String type = CapitalCode._DEPOSIT.getCode();
        String secondType = CapitalCode._DEPOSIT_AGENT.getCode();
        BigDecimal balance = agentService.updateCenterBalance(account, amount);
        agentLogService.saveTradeLog(account, type, secondType, okStatus, amount, orderNo, balance, ip, remarks, CapitalCode._DEPOSIT_AGENT.getMessage());
    }

    public BigDecimal updateWalletBalance(String account, String walletType, BigDecimal amount) {
        BigDecimal balance = BigDecimal.ZERO;
        if (WalletCode._COMMISSION.getCode().equals(walletType)) {
            balance = agentService.updateCommissionBalance(account, amount.negate());
        } else if (WalletCode._OTHER.getCode().equals(walletType)) {
            balance = agentService.updateOtherBalance(account, amount.negate());
        }
        return balance;
    }

    public PageData getAgentTransferSubOrders(String agent, String member, Integer okStatus, Integer page, Integer pageSize, String startTime, String endTime) {
        Sort.Order[] orders = {Sort.Order.desc("id"), Sort.Order.desc("createTime")};
        Sort sort = Sort.by(Arrays.asList(orders));
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Specification<AgentTransferSubOrder> spec = new Specification<AgentTransferSubOrder>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<AgentTransferSubOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 增加筛选条件
                Predicate predicate = cb.conjunction();
                if (!StringUtils.isEmpty(agent)) {
                    predicate.getExpressions().add(cb.equal(root.get("agent").as(String.class), agent));
                }
                if (!StringUtils.isEmpty(member)) {
                    predicate.getExpressions().add(cb.equal(root.get("member").as(String.class), member));
                }
                if (null != okStatus) {
                    predicate.getExpressions().add(cb.equal(root.get("okStatus").as(Integer.class), okStatus));
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
        Page<AgentTransferSubOrder> pages = agentTransferSubDao.findAll(spec, pageable);
        PageData pageData = PageData.builder(pages);
        List<TransferSubMemberDto> dtos = this.assembleTransferSubData((List<AgentTransferSubOrder>) pageData.getContents());
        pageData.setContents(dtos);
        return pageData;
    }

    private List<TransferSubMemberDto> assembleTransferSubData(List<AgentTransferSubOrder> list) {
        List<TransferSubMemberDto> dtos = new ArrayList<TransferSubMemberDto>();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        TransferSubMemberDto dto = null;
        Iterator<AgentTransferSubOrder> itrator = list.iterator();
        while (itrator.hasNext()) {
            AgentTransferSubOrder order = itrator.next();
            dto = new TransferSubMemberDto();
            dto.setCreateTime(order.getCreateTime());
            dto.setOkStatus(order.getOkStatus());
            dto.setAmount(order.getAmount());
            dto.setAccount(order.getMember());
            dto.setRemarks(order.getRemarks());
            dto.setWalletType(order.getWalletType());
            dtos.add(dto);
        }
        return dtos;
    }
}
