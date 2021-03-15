package com.esports.log.service;

import com.esports.log.bean.db1.AgentTradeLog;
import com.esports.log.dao.db1.AgentTradeLogDao;
import com.esports.log.dto.AgentTradeDto;
import com.esports.utils.DateUtils;
import com.esports.utils.IPUtils;
import com.esports.utils.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class AgentTradeLogManager {

    @Autowired
    private AgentTradeLogDao agentTradeLogDao;

    public void log(String walletType, String type, String secondType, Integer okStatus, BigDecimal amount,
                    String orderNo, String account, BigDecimal balance, String ip, String remarks) {
        AgentTradeLog log = new AgentTradeLog();
        Timestamp createTime = new Timestamp(System.currentTimeMillis());
        log.setWalletType(walletType);
        log.setCreateTime(createTime);
        log.setAccount(account);
        log.setOperator(account);
        log.setRemarks(remarks);
        log.setIp(IPUtils.ipToLong(ip));
        log.setOkStatus(okStatus);
        log.setOrderNo(orderNo);
        log.setBalance(balance);
        log.setAmount(null == amount ? BigDecimal.ZERO : amount);
        log.setType(type);
        log.setSecondType(secondType);
        agentTradeLogDao.save(log);
    }

    public PageData getOrders(String walletType, String account, Integer okStatus, String type, Integer page, Integer pageSize, String startTime, String endTime) {
        Pageable pageable = PageRequest.of(page, pageSize);
        startTime = DateUtils.getDayStartTime(startTime);
        endTime = DateUtils.getDayEndTime(endTime);
        Page<AgentTradeLog> pages = agentTradeLogDao.findAll(walletType, account, okStatus, type, startTime, endTime, pageable);
        PageData pageData = PageData.builder(pages);
        List<AgentTradeDto> dtos = this.assembleData((List<AgentTradeLog>) pageData.getContents());
        pageData.setContents(dtos);
        return pageData;
    }

    private List<AgentTradeDto> assembleData(List<AgentTradeLog> list) {
        List<AgentTradeDto> dtos = new ArrayList<AgentTradeDto>();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        AgentTradeDto dto = null;
        Iterator<AgentTradeLog> itrator = list.iterator();
        while (itrator.hasNext()) {
            AgentTradeLog log = itrator.next();
            dto = new AgentTradeDto();
            dto.setType(log.getSecondType());
            dto.setCreateTime(log.getCreateTime());
            dto.setOkStatus(log.getOkStatus());
            dto.setAmount(log.getAmount());
            dto.setBalance(log.getBalance());
            dto.setOrderNo(log.getOrderNo());
            dtos.add(dto);
        }
        return dtos;
    }
}
