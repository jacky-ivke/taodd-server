package com.esports.log.service;

import com.esports.log.bean.db1.MemberPlayGameLog;
import com.esports.log.dao.db1.MemberPlayGameLogDao;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class MemberPlayGameLogManager {

    @Autowired
    private MemberPlayGameLogDao memberPlayGameLogDao;

    public void log(String account, String apiCode, String gameCode, Long ip) {
        MemberPlayGameLog log = new MemberPlayGameLog();
        log.setIp(ip);
        log.setAccount(account);
        log.setCreateTime(new Timestamp(System.currentTimeMillis()));
        log.setAppCode(apiCode);
        log.setGameCode(gameCode);
        memberPlayGameLogDao.save(log);
    }
}
