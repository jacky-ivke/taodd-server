package com.esports.external.handler.yabo.live.dto;

import lombok.Data;

@Data
public class ReqBetRecordHistoryDTO{
    private String startTime;
    private String endTime;
    private Integer pageIndex;
    private Long timestamp;
}
