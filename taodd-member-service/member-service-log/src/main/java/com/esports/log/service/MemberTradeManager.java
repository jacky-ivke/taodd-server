package com.esports.log.service;

import com.esports.log.bean.db1.MemberTradeLog;
import com.esports.log.dao.db1.MemberTradeLogDao;
import com.esports.log.dto.MemberTradeDto;
import com.esports.utils.DateUtils;
import com.esports.utils.IPUtils;
import com.esports.utils.PageData;
import lombok.extern.slf4j.Slf4j;
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

@Service
@Slf4j
public class MemberTradeManager {

    @Autowired
    private MemberTradeLogDao memberTradeLogDao;


    public void log(String type, String secondType, Integer okStatus, BigDecimal amount,
                    String orderNo, String account,BigDecimal balance, String ip, String remarks, String postscript) {
        MemberTradeLog log = new MemberTradeLog();
        Timestamp createTime = new Timestamp(System.currentTimeMillis());
        log.setCreateTime(createTime);
        log.setAccount(account);
        log.setOperator(account);
        log.setRemarks(remarks);
        log.setIp(IPUtils.ipToLong(ip));
        log.setOkStatus(okStatus);
        log.setOrderNo(orderNo);
        log.setBalance(balance);
        log.setAmount(null == amount? BigDecimal.ZERO : amount);
        log.setType(type);
        log.setSecondType(secondType);
        log.setPostscript(postscript);
        memberTradeLogDao.save(log);
    }

    public void updaetLog(String orderNo, Integer okStatus, BigDecimal balance, String remarks) {
        memberTradeLogDao.updateStatus(orderNo, okStatus, remarks, balance);
    }

    @SuppressWarnings("unchecked")
    public PageData getOrders(String account, Integer okStatus, String type, Integer page, Integer pageSize, String startTime, String endTime){
        Pageable pageable = PageRequest.of(page, pageSize);
        PageData pageData = null;
        try {
            startTime = StringUtils.isEmpty(startTime)? "" : DateUtils.getDayStartTime(startTime);
            endTime = StringUtils.isEmpty(endTime)?  "" : DateUtils.getDayEndTime(endTime);
            Page<MemberTradeLog> pages = memberTradeLogDao.findAll(account, okStatus, type, startTime, endTime, pageable);
            pageData = PageData.builder(pages);
            List<MemberTradeDto> dtos = this.assembleData((List<MemberTradeLog>)pageData.getContents());
            pageData.setContents(dtos);
        } catch (Exception e) {
            pageData = PageData.builder(null, pageSize, 0, 1, page);
            log.error("【获取会员交易明细分页数据】账号:{}, 异常信息:{}", account, e.getMessage());
        }
        return pageData;
    }

    private List<MemberTradeDto> assembleData(List<MemberTradeLog> list){
        List<MemberTradeDto> dtos = new ArrayList<MemberTradeDto>();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        MemberTradeDto dto = null;
        Iterator<MemberTradeLog> itrator = list.iterator();
        while (itrator.hasNext()) {
            MemberTradeLog log = itrator.next();
            dto = new MemberTradeDto();
            dto.setType(log.getSecondType());
            dto.setCreateTime(log.getCreateTime());
            dto.setOkStatus(log.getOkStatus());
            dto.setAmount(log.getAmount());
            dto.setBalance(log.getBalance());
            dto.setOrderNo(log.getOrderNo());
            dto.setRemarks(log.getPostscript());
            dtos.add(dto);
        }
        return dtos;
    }
}
