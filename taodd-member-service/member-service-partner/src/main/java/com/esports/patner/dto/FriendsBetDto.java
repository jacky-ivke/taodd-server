package com.esports.patner.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FriendsBetDto {

    /**
     * 投注总额
     */
    private BigDecimal betTotalAmount;

    /**
     * 佣金总额
     */
    private BigDecimal inviteCommissionAmount;

    /**
     * 一级好友投注
     */
    private BigDecimal firstBetTotalAmount = BigDecimal.ZERO;

    /**
     * 二级好友投注
     */
    private BigDecimal secondBetTotalAmount = BigDecimal.ZERO;

    /**
     * 三级好友投注
     */
    private BigDecimal thirdBetTotalAmount = BigDecimal.ZERO;
}
