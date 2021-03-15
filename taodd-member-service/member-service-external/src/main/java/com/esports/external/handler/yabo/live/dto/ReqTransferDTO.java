package com.esports.external.handler.yabo.live.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 *
 */
@Data
public class ReqTransferDTO {
    private String loginName;
    private String transferNo;
    private BigDecimal amount;
    private Long timestamp;
}





