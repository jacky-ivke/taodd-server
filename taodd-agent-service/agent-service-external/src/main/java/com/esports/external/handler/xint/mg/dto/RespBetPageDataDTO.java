package com.esports.external.handler.xint.mg.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RespBetPageDataDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("data")
	RespGameListDTO[] records;
	
	public boolean hasNextPage(Integer pageSize) {
		boolean success = false;
		if(null == pageSize || null == this.records) {
			return success;
		}
		//预判有分页数据
		if(this.records.length == pageSize) {
			success = true;
		}
		return success;
	}
}
