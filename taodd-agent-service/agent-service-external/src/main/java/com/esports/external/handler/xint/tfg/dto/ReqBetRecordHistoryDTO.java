package com.esports.external.handler.xint.tfg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReqBetRecordHistoryDTO{
	
	@JsonProperty("from_datetime")
	private String startTime;
	
	@JsonProperty("to_datetime")
    private String endTime;
	
	@JsonProperty("page")
    private Long pageIndex;
    
	@JsonProperty("page_size")
    private Integer pageSize;
    
}

