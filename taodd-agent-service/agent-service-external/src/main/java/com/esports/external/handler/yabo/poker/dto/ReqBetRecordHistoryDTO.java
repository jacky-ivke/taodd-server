package com.esports.external.handler.yabo.poker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReqBetRecordHistoryDTO{
	@JsonProperty("beginTime")
    private Long startTime;
    private Long endTime;
    private Long pageNum;
    private Integer pageSize;
}
