package com.esports.external.handler.yabo.live.dto;

import lombok.Data;

@Data
public class RequestParamDTO {

    private String merchantCode;

    private String params;

    private String signature;
}
