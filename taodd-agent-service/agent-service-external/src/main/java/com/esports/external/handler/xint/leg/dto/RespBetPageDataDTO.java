package com.esports.external.handler.xint.leg.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RespBetPageDataDTO  implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String code;
	
	private Integer count;
	
	private RespGameListDTO list;
	
	private Long start;	
	
	private Long end;
}
