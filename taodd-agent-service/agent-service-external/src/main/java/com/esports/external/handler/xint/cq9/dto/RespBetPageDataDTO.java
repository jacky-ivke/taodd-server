package com.esports.external.handler.xint.cq9.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RespBetPageDataDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("TotalSize")
	private Integer totalRecords = 0;
	
	@JsonProperty("Data")
	private RespGameListDTO[] records;
	
	@JsonIgnore
	public Integer getTotalPages(Integer pageSize) {
		Integer totalPage = 0;
		if(null == this.totalRecords) {
			return totalPage;
		}
		totalPage = (this.totalRecords % pageSize == 0? this.totalRecords / pageSize : (this.totalRecords/ pageSize)+1);
		return totalPage;
	}
}
