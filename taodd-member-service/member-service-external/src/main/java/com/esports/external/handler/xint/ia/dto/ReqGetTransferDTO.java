package com.esports.external.handler.xint.ia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 */
@Data
public class ReqGetTransferDTO{

	/**
	 * 密钥
	 */
	@JsonProperty("secret_key")
    private String secretKey;
    
    /**
     * token
     */
	@JsonProperty("operator_token")
    private String operatorToken;
	
	/**
	 * 玩家账号
	 */
	@JsonProperty("player_name")
	private String loginName;
	
	/**
	 * 交易编号
	 */
	private String traceId;
	
	private String wallet_code = "gf_ia_esport";
}
