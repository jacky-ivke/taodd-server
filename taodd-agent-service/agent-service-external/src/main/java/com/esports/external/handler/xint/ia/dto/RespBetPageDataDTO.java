package com.esports.external.handler.xint.ia.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RespBetPageDataDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@JsonProperty("last_record_time")
	private Long lastVersionKey;

	@JsonProperty("betlogs")
	RespGameListDTO[] records;
	
	public Integer getTotalRecords() {
		Integer total = 0;
		if(null == this.records) {
			return total;
		}
		total = this.records.length;
		return total;
	}
}
