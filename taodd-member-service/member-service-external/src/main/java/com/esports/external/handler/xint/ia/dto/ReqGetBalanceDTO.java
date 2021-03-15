package com.esports.external.handler.xint.ia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 */
@Data
public class ReqGetBalanceDTO {
	
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
	 * 指定钱包代码
	 */
	@JsonProperty("wallet_code")
	private String walletCode = "gf_ia_esport";
    
}
