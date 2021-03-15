package com.esports.external.handler.xint.mg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RespMetaDataDTO {

	@JsonProperty("round_id")
	private String roundId;
	
	@JsonProperty("item_id")
	private String gameId;
	
	@JsonProperty("ext_item_id")
	private String gameName;
}
