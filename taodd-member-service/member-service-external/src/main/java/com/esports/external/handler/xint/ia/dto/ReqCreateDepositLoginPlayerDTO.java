package com.esports.external.handler.xint.ia.dto;

import com.esports.constant.GlobalSourceCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReqCreateDepositLoginPlayerDTO {
	
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
	 * 游戏代码
	 */
	@JsonProperty("game_code")
	private String gameCode;
	
	/**
	 * 玩家账号
	 */
	@JsonProperty("player_name")
	private String loginName;
	
	/**
	 * 游戏语言
	 */
	private String language;
	
	@JsonIgnore
	public String getPlatform(Integer deviceType) {
		if(GlobalSourceCode._PC.getCode().equals(deviceType.toString())) {
			return "ia_1";
		}
		return "ia_2";
		
	}
}
