package com.esports.log.service;

import com.esports.log.bean.db1.AgentLoginLog;
import com.esports.log.dao.db1.AgentLoginLogDao;
import com.esports.utils.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class AgentLoginLogManager {

    @Autowired
    private AgentLoginLogDao agentLoginLogDao;

    public void log(String account, String client, Integer platform, Long ip) {
        AgentLoginLog log = new AgentLoginLog();
        String area = IPUtils.getArea(IPUtils.longToIp(ip));
        log.setIp(ip);
        log.setArea(area);
        log.setAccount(account);
        log.setCreateTime(new Timestamp(System.currentTimeMillis()));
        log.setClient(client);
        log.setPlatform(platform);
        agentLoginLogDao.save(log);
    }

}
