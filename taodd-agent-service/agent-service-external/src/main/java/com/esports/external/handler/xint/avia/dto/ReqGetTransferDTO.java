package com.esports.external.handler.xint.avia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 */
@Data
public class ReqGetTransferDTO {

	@JsonProperty("ID")
    private String orderNo;

    @JsonProperty("Currency")
    private String currency;

}
