package com.esports.api.fallback;

import com.esports.api.log.AgentLogService;

import java.math.BigDecimal;

/**
 * 代理日志服务降级处理
 */
public class LogServiceFallback implements AgentLogService {

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
    public void saveTradeLog(String account, String walletType, String logType, String secondType, Integer okStatus, BigDecimal amount, String orderNo, BigDecimal balance, String ip, String remarks) {

    }

    @Override
    public void saveTradeLog(String account, String logType, String secondType, Integer okStatus, BigDecimal amount, String orderNo, BigDecimal balance, String ip, String remarks, String postscript) {

    }

}
