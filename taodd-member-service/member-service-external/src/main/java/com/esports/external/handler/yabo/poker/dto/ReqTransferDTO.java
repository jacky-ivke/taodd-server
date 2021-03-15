package com.esports.external.handler.yabo.poker.dto;

import lombok.Data;

/**
 *
 */
@Data
public class ReqTransferDTO {
    private String memberId;
    private Integer money;
    private String orderId;
    private String memberName;
    private String memberPwd;
    private Integer deviceType;
    private String memberIp;
}
