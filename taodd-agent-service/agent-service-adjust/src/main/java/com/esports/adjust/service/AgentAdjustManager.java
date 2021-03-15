package com.esports.adjust.service;

import com.esports.adjust.bean.db1.AdjustOrder;
import com.esports.adjust.dao.db1.AgentAdjustDao;
import com.esports.adjust.dto.AdjustDto;
import com.esports.adjust.dto.AgentAdjustDto;
import com.esports.adjust.dto.AgentAdjustRecordsDto;
import com.esports.api.center.AgentService;
import com.esports.constant.OrderCode;
import com.esports.utils.DateUtils;
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
public class AgentAdjustManager {

    @Autowired
    private AgentAdjustDao agentAdjustDao;

    @Autowired
    private AgentService agentService;


    public boolean checkAccount(String account) {
        boolean success = true;
        String player = agentService.getAccount(account);
        if (StringUtils.isEmpty(player)) {
            success = false;
        }
        return success;
    }


    public boolean checkAdjustMember(String account, String member) {
        //验证代理调线会员是否已在审核中，避免重复提交审核
        boolean success = false;
        Integer okStatus = OrderCode._PENDING.getCode();
        PageData data = this.getAdjustRecords(account, member, okStatus, 0, 1, null, null);
        if (null != data && !CollectionUtils.isEmpty(data.getContents())) {
            success = true;
        }
        return success;
    }

    public PageData getAdjustRecords(String agent, String member, Integer okStatus, Integer page, Integer pageSize, String startTime, String endTime) {
        Sort.Order[] orders = {Sort.Order.desc("id"), Sort.Order.desc("createTime")};
        Sort sort = Sort.by(Arrays.asList(orders));
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Specification<AdjustOrder> spec = new Specification<AdjustOrder>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<AdjustOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 增加筛选条件
                Predicate predicate = cb.conjunction();
                if (!StringUtils.isEmpty(member)) {
                    predicate.getExpressions().add(cb.equal(root.get("account").as(String.class), member));
                }
                if (!StringUtils.isEmpty(agent)) {
                    predicate.getExpressions().add(cb.equal(root.get("agent").as(String.class), agent));
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
        Page<AdjustOrder> pages = agentAdjustDao.findAll(spec, pageable);
        PageData pageData = PageData.builder(pages);
        List<AgentAdjustRecordsDto> dtos = this.assembleAdjustRecordsData((List<AdjustOrder>) pageData.getContents());
        pageData.setContents(dtos);
        return pageData;
    }

    public List<AgentAdjustRecordsDto> assembleAdjustRecordsData(List<AdjustOrder> list) {
        List<AgentAdjustRecordsDto> dtos = new ArrayList<AgentAdjustRecordsDto>();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        AgentAdjustRecordsDto dto = null;
        Iterator<AdjustOrder> itrator = list.iterator();
        while (itrator.hasNext()) {
            AdjustOrder order = itrator.next();
            dto = new AgentAdjustRecordsDto();
            dto.setCreateTime(order.getCreateTime());
            dto.setAccount(order.getAccount());
            dto.setDepositAmount(null != order.getDepositAmount() ? order.getDepositAmount() : BigDecimal.ZERO);
            dto.setProfitAmount(null != order.getProfitAmount() ? order.getProfitAmount() : BigDecimal.ZERO);
            dto.setBalance(null != order.getBalance() ? order.getBalance() : BigDecimal.ZERO);
            dto.setApiBalance(null != order.getApiBalance() ? order.getApiBalance() : BigDecimal.ZERO);
            dto.setFirstDeposit(null != order.getFirstDeposit() ? order.getFirstDeposit() : BigDecimal.ZERO);
            dto.setOkStatus(order.getOkStatus());
            dto.setRemarks(order.getRemarks());
            dtos.add(dto);
        }
        return dtos;
    }

    public void saveAgentAdjust(String account, String member, Integer source, Integer type, String remarks, String guideLink, List<String> imgs) {

    }

    public AgentAdjustDto getAgentAdjust(String account) {
        AgentAdjustDto dto = new AgentAdjustDto();
        Integer subMember = agentService.getTotalSubMember(account);
        Map<String, Object> map = agentAdjustDao.getAdjustInfo(account);
        AdjustDto adjust = JsonUtil.map2Object(map, AdjustDto.class);
        Integer adjustMember = null != adjust && null != adjust.getAdjustMember() ? adjust.getAdjustMember() : 0;
        Integer adjustTotal = null != adjust && null != adjust.getAdjustTotal() ? adjust.getAdjustTotal() : 0;
        Integer currentMonthAdjust = null != adjust && null != adjust.getCurrentMonthAdjust() ? adjust.getCurrentMonthAdjust() : 0;
        Integer originalMember = subMember - adjustMember;
        originalMember = originalMember <= 0 ? 1 : originalMember;
        BigDecimal rate = new BigDecimal(adjustMember).divide(new BigDecimal(originalMember)).multiply(new BigDecimal("100")).setScale(0);
        dto.setAdjustTotal(adjustTotal);
        dto.setCurrentMonthAdjust(currentMonthAdjust);
        dto.setRate(rate);
        return dto;
    }
}
