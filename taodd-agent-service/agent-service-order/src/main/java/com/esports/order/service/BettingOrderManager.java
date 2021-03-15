package com.esports.order.service;

import com.esports.order.dao.db1.BettingOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BettingOrderManager {

    @Autowired
    private BettingOrderDao bettingOrderDao;

    public BigDecimal getMemberProfitAmount(String account) {

        BigDecimal amount = bettingOrderDao.getMemberProfitAmount(account);
        return (null == amount ? BigDecimal.ZERO : amount);
    }

}
