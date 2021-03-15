package com.esports.api.fallback;

import com.esports.api.log.MemberLogService;

import java.math.BigDecimal;

/**
 * 指定对应的服务降级实现类
 */
public class LogServiceFallback implements MemberLogService {

    @Override
    public String getLastLoginTime(String account) {
        return null;
    }

    @Override
    public void saveLoginLog(String account, String client, Integer platform, Long ip) {

    }

    @Override
    public void saveEventLog(String account, String type, String source, String target, Long ip) {

    }

    @Override
    public void saveTradeLog(String account, String logType, String secondType, Integer okStatus, BigDecimal amount, String orderNo, BigDecimal balance, String ip, String remarks, String postscript) {

    }

    @Override
    public void savePlayGameLog(String account, String apiCode, String gameCode, Long ip) {

    }
}
