package com.esports.external.handler.xint.pt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 */
@Data
public class ReqGetTransferDTO{

	@JsonProperty("externaltransactionid")
	private String orderNo;
}
