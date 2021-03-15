package com.esports.external.handler.xint.mg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReqBetRecordHistoryDTO{
	
	/**
	 * 代理賬號
	 */
	@JsonProperty("company_id")
	private String partenId;
	
	@JsonProperty("start_time")
    private String startTime;
	
	@JsonProperty("end_time")
    private String endTime;
	
	@JsonProperty("include_transfers")
	private Boolean transfer=false;
	
	@JsonProperty("include_end_round")
	private Boolean endRound=true;
	
	@JsonProperty("page")
    private Long pageIndex;
    
	@JsonProperty("page_size")
    private Integer pageSize;
    
}
