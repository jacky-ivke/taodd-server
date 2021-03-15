package com.esports.external.handler.xint.pt.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RespBetPageDataDTO  implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@JsonProperty("page_size")
	private Integer pageSize;
	
	@JsonProperty("page")
	private Long pageIndex;
	
	@JsonProperty("total_pages")
	private Integer totalPage;
	
	@JsonProperty("total_results")
	private Integer totalRecord;
}
