package com.esports.external.handler.yabo.live.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ReqCreateDepositLoginPlayerDTO {
    private String loginName;
    private String loginPassword;
    private String nickName;
    private Integer deviceType;
    private Integer lang;
    private String backurl;
    private String domain="";
    private Integer showExit=1;
    private String gameTypeId;
    private Long timestamp;
    private String transferNo;
    private BigDecimal amount;
    
    
}
