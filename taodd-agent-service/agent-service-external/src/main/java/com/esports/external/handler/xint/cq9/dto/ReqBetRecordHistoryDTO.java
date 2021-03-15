package com.esports.external.handler.xint.cq9.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReqBetRecordHistoryDTO{
	
	@JsonProperty("starttime")
    private String startTime;
	
	@JsonProperty("endtime")
    private String endTime;
    
    @JsonProperty("page")
    private Long pageIndex;
    
    @JsonProperty("pagesize")
    private Integer pageSize;
}
