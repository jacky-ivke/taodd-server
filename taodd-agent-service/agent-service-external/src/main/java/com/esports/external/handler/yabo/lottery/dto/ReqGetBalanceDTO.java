package com.esports.external.handler.yabo.lottery.dto;

import lombok.Data;

/**
 *
 */
@Data
public class ReqGetBalanceDTO {

    private String member;

    private String merchant;
    
    private Long timestamp;
}
