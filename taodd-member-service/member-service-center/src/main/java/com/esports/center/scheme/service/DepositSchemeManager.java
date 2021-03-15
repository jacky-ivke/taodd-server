package com.esports.center.scheme.service;

import com.esports.center.scheme.bean.db1.DepositScheme;
import com.esports.center.scheme.dao.db1.DepositSchemeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DepositSchemeManager {

    @Autowired
    private DepositSchemeDao depositSchemeDao;

    /**
     * 会员单日存款申请总次数（成功和失败），默认100次
     */
    private static Integer DEPOSIT_TOTAL_NUM = 100;

    /**
     * 10分钟类允许存款申请次数,默认5次
     */
    private static Integer DEPOSIT_NUM = 5;

    /**
     * 10分钟之内存款次数耗尽，失败率预警值，默认失败率超过50%限制提交申请
     */
    private static BigDecimal DEPOSIT_FAILURE_RATE = new BigDecimal("50");

    public DepositScheme getDepositScheme() {
        List<DepositScheme> list = depositSchemeDao.findAll();
        DepositScheme depositScheme = CollectionUtils.isEmpty(list) ? new DepositScheme() : list.get(0);
        Integer depositTotalNum = null != depositScheme ? depositScheme.getDepositTotalNum() : 0;
        Integer depositNum = null != depositScheme ? depositScheme.getDepositNum() : 0;
        BigDecimal failureRate = null != depositScheme ? depositScheme.getDepositFailureRate() : BigDecimal.ZERO;
        depositTotalNum = depositTotalNum > 0 ? depositTotalNum : DEPOSIT_TOTAL_NUM;
        depositNum = depositNum > 0 ? depositNum : DEPOSIT_NUM;
        failureRate = failureRate.compareTo(BigDecimal.ZERO) > 0 ? failureRate : DEPOSIT_FAILURE_RATE;
        DepositScheme scheme = new DepositScheme();
        scheme.setDepositTotalNum(depositTotalNum);
        scheme.setDepositNum(depositNum);
        scheme.setDepositFailureRate(failureRate);
        return depositScheme;
    }
}
