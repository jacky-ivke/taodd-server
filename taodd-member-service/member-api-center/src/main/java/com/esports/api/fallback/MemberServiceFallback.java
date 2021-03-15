package com.esports.api.fallback;

import com.esports.api.center.MemberService;

import java.math.BigDecimal;

public class MemberServiceFallback implements MemberService {

    @Override
    public String getRakeScheme(String gradeCode) {
        return "";
    }

    @Override
    public Integer getVip(String account) {
        return 0;
    }

    @Override
    public String getGradeCode(String account) {
        return "";
    }

    @Override
    public String getRakeSchemeCode(String account) {
        return null;
    }

    @Override
    public String getAccount(String account) {
        return "";
    }

    @Override
    public String getExtData(String account, Integer source, String ip) {
        return "";
    }

    @Override
    public String getFriends(String account) {
        return "";
    }

    @Override
    public boolean checkAccount(String account) {
        return false;
    }

    @Override
    public BigDecimal getBalance(String account) {
        return BigDecimal.ZERO;
    }

    @Override
    public void createAccount(String account, String password, String inviter, Integer source, String ip) {

    }

    @Override
    public BigDecimal updateBalance(String account, BigDecimal amount) {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal updateBalanceAndInterest(String account, BigDecimal balance, BigDecimal interest) {
        return BigDecimal.ZERO;
    }
}
