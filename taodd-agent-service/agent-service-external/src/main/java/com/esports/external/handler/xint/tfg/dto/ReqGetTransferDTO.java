package com.esports.external.handler.xint.tfg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 */
@Data
public class ReqGetTransferDTO{

	@JsonProperty("reference_no")
	private String orderNo;
	
	private Long page = 1L;
	
	@JsonProperty("page_size")
	private Integer pageSize = 10;
}
