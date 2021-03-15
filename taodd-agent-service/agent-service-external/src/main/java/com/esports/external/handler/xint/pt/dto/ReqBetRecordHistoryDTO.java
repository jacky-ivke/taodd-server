package com.esports.external.handler.xint.pt.dto;

import lombok.Data;

@Data
public class ReqBetRecordHistoryDTO{
	
	private Boolean exitgame=false;
	
	private Boolean showdetailedinfo=false;
	
	private Boolean showbonustype=false;
	
	private Boolean excludezero;
	
	private Boolean progressiveonly;
	
	private String startdate;
	
	private String enddata;
	
	private String clientinfo;
	
	private Long page;
	
	private Integer perPage;
}
