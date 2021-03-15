package com.esports.external.handler.xint.ky.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 *
 */
@Data
public class ReqTransferDTO {
    private String s;
    private String account;
    private BigDecimal money;
    private String orderid;
}
