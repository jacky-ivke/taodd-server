package com.esports.log.service;

import com.esports.constant.LogTypeEnum;
import com.esports.log.bean.db1.AgentLog;
import com.esports.log.dao.db1.AgentLogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class AgentLogManager {

    @Autowired
    private AgentLogDao agentLogDao;

    public AgentLog saveInfo(AgentLog agentLog) {
        if (null != agentLog) {
            agentLogDao.save(agentLog);
        }
        return agentLog;
    }

    public void log(String account, String type, String source, String target, Long ip) {
        AgentLog log = new AgentLog();
        String remarks = String.format(LogTypeEnum.getRemarks(type), account);
        log.setType(type);
        log.setAccount(account);
        log.setCreateTime(new Timestamp(System.currentTimeMillis()));
        log.setSource(source);
        log.setIp(ip);
        log.setTarget(target);
        log.setRemarks(remarks);
        agentLogDao.save(log);
    }

}
