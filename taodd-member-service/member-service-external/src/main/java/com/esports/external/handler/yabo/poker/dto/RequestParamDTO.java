package com.esports.external.handler.yabo.poker.dto;

import lombok.Data;

@Data
public class RequestParamDTO {

    private String agent;

    private String randno;

    private String sign;
    
    private Long timestamp;
}
