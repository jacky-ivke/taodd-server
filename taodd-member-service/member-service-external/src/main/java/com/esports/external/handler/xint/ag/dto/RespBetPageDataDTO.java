package com.esports.external.handler.xint.ag.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RespBetPageDataDTO  implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 每页记录数
	 */
	@JsonProperty("num_per_page")
	private Integer pageSize;
	
	
	/**
	 * 当前页
	 */
	@JsonProperty("currentpage")
	private Long pageIndex;
	
	/**
	 * 总页数
	 */
	@JsonProperty("total")
	private Integer totalPage;
	
	/**
	 *  总计数据,记录条数
	 */
	@JsonProperty("totalpage")
	private Integer totalRecords;
	
	/**
	 * 当前页记录数
	 */
	@JsonProperty("perpage")
	private Integer records;
	
}
