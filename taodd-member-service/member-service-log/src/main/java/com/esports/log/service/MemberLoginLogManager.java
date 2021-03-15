package com.esports.log.service;

import com.esports.log.bean.db1.MemberLoginLog;
import com.esports.log.dao.db1.MemberLoginLogDao;
import com.esports.utils.DateUtils;
import com.esports.utils.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;

@Service
public class MemberLoginLogManager {

    @Autowired
    private MemberLoginLogDao loginLogDao;

    public void log(String account, String client, Integer platform, Long ip) {
        MemberLoginLog log = new MemberLoginLog();
        String area = IPUtils.getArea(IPUtils.longToIp(ip));
        log.setIp(ip);
        log.setArea(area);
        log.setAccount(account);
        log.setCreateTime(new Timestamp(System.currentTimeMillis()));
        log.setClient(client);
        log.setPlatform(platform);
        loginLogDao.save(log);
    }

    public String getLastLoginTime(String account) {
        if (StringUtils.isEmpty(account)) {
            return null;
        }
        Timestamp loginTime = loginLogDao.getLastLoginTime(account);
        String strTime = null != loginTime? DateUtils.dateFormartString(loginTime, DateUtils.Pattern_Date):null;
        return strTime;
    }
}
