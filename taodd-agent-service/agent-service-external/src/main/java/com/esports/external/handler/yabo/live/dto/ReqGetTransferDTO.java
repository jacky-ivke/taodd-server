package com.esports.external.handler.yabo.live.dto;

import lombok.Data;

/**
 *
 */
@Data
public class ReqGetTransferDTO{

    private String transferNo;

    private String loginName;
    
    private Long timestamp;
}
