package com.esports.external.handler.yabo.slots.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReqBetRecordHistoryDTO{
	
	@JsonProperty("beginTime")
    private Long startTime;
    private Long endTime;
    private Integer pageNum;
    private Integer pageSize;
}
