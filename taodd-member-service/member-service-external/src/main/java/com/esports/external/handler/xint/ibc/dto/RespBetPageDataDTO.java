package com.esports.external.handler.xint.ibc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RespBetPageDataDTO  implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@JsonProperty("last_version_key")
	private Long lastVersionKey;
	
	@JsonProperty("BetDetails")
	private RespGameListDTO[] records;
}
