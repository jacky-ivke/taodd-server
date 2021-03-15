package com.esports.external.handler.yabo.lottery.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 *
 */
@Data
public class ReqTransferDTO {
    private String member;
    private String merchantAccount;
    private Integer transferType;
    private BigDecimal amount;
    private String notifyId;
    private Long timestamp;
}
