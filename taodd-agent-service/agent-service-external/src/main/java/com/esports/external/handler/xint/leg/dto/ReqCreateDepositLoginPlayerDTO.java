package com.esports.external.handler.xint.leg.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReqCreateDepositLoginPlayerDTO {
    private String s;
    private String account;
    private BigDecimal money;
    private String orderid;
    private String ip;
    private String lineCode;
    private String KindID;
}
