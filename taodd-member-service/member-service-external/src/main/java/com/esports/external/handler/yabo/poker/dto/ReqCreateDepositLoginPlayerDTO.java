package com.esports.external.handler.yabo.poker.dto;

import lombok.Data;

@Data
public class ReqCreateDepositLoginPlayerDTO {
    private String memberId;
    private String memberName;
    private String memberPwd;
    private Integer deviceType;
    private String memberIp;
    private String gameId;
}
