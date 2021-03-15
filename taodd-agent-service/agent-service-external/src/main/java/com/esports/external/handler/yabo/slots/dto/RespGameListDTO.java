package com.esports.external.handler.yabo.slots.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RespGameListDTO {
	
	
	private String gameId;
	
	private String gameName;
	
	private Integer gameCategory;
	
	private Integer status;
	
	private Integer order;
	
	private String iconUrl;

}
