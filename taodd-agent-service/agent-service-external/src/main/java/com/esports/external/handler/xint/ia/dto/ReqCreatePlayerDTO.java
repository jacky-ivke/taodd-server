package com.esports.external.handler.xint.ia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 创建游戏账号
 * @author jacky
 *
 */
@Data
public class ReqCreatePlayerDTO {

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
     * 会员密码
     */
    private String currency="CNY";
}
