package com.esports.center.vip.service;

import com.esports.center.vip.bean.db1.VipRule;
import com.esports.center.vip.dao.db1.VipRuleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VipRuleManager {

    @Autowired
    private VipRuleDao vipRuleDao;

    public String getVipTitle(Integer vip) {
        VipRule rule = vipRuleDao.findByGrade(vip);
        String title = null != rule ? rule.getTitle() : "";
        return title;
    }

}
