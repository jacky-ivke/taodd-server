package com.esports.external.handler.xint.tfg.dto;

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
	 * 游戏账号
	 */
	@JsonProperty("member_code")
    private String loginName;
}
