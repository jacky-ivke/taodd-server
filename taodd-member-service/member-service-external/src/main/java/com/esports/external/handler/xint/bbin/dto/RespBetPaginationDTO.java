package com.esports.external.handler.xint.bbin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RespBetPaginationDTO {
	
	/**
	 * 当前页
	 */
	@JsonProperty("Page")
	private Long pageIndex;
	
	@JsonProperty("TotalPage")
	private Integer totalPages;
	
	/**
	 *  总计数据,记录条数
	 */
	@JsonProperty("TotalNumber")
	private Long totalRecords;
}
