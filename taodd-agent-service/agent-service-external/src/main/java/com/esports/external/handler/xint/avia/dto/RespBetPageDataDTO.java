package com.esports.external.handler.xint.avia.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RespBetPageDataDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@JsonProperty("PageSize")
	private Integer pageSize;
	
	@JsonProperty("PageIndex")
	private Integer pageIndex = 1;
	
	@JsonProperty("RecordCount")
	private Long totalRecord = 0L;
	
	private Object data;
	
	@JsonProperty("list")
	private RespGameListDTO[] records;
}
