package com.esports.external.handler.yabo.lottery.dto;

import lombok.Data;

@Data
public class ReqBetRecordHistoryDTO{
    private String startTime;
    private String endTime;
    private String merchantAccount="";
    private Boolean agency=Boolean.TRUE;
    private Integer pageSize;
    private Long lastOrderId;
}
