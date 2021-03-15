package com.esports.scheme.service;

import com.esports.scheme.bean.db1.CommissionScheme;
import com.esports.scheme.dao.db1.CommissionSchemeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CommissionSchemeManager {

    @Autowired
    private CommissionSchemeDao commissionSchemeDao;

    @Autowired
    private CommissionCfgManager commissionCfgManager;

    public boolean checkCommissionRule(String commissionCode, BigDecimal drawAmount) {
        boolean success = true;
        CommissionScheme scheme = commissionSchemeDao.findBySchemeCode(commissionCode);
        if (null == scheme) {
            return success;
        }
        BigDecimal minAmount = scheme.getMinAmount();
        BigDecimal maxAmount = scheme.getMaxAmount();
        if (null != minAmount && null != maxAmount && maxAmount.compareTo(minAmount) > 0) {
            if (drawAmount.compareTo(minAmount) < 0 || drawAmount.compareTo(maxAmount) > 0) {
                success = false;
            }
        }
        return success;
    }
}