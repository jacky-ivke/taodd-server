package com.esports.log.service;


import com.esports.constant.LogTypeEnum;
import com.esports.log.bean.db1.MemberLog;
import com.esports.log.dao.db1.MemberLogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class MemberLogManager {

    @Autowired
    private MemberLogDao memberLogDao;

    public void log(String account, String type, String source, String target, Long ip) {
        MemberLog log = new MemberLog();
        Timestamp createTime = new Timestamp(System.currentTimeMillis());
        String remarks = String.format(LogTypeEnum.getRemarks(type), account);
        log.setType(type);
        log.setAccount(account);
        log.setCreateTime(createTime);
        log.setSource(source);
        log.setIp(ip);
        log.setTarget(target);
        log.setRemarks(remarks);
        memberLogDao.save(log);
    }
}
