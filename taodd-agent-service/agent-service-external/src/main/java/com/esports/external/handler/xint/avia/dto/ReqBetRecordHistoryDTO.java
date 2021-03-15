package com.esports.external.handler.xint.avia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReqBetRecordHistoryDTO{

	/**
	 * 查询订单类型
	 */
	@JsonProperty("OrderType")
	private String orderType = "All";
	
	/**
	 *开始时间
	 */
	@JsonProperty("StartAt")
    private String startTime;
	
	/**
	 * 结束时间
	 */
	@JsonProperty("EndAt")
    private String endTime;
	
	/**
	 * 用户名
	 */
	@JsonProperty("UserName")
    private String userName;
	
	/**
	 * 分页查询的页码，默认为1
	 */
	@JsonProperty("PageIndex")
    private Long pageIndex = 1L;
	
	/**
	 * 分页大小
	 */
	@JsonProperty("PageSize")
    private Integer pageSize = 20;
}
