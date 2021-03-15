package com.esports.external.handler.xint.cq9.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 *
 */
@Data
public class ReqGetBalanceDTO {

	/**
	 * 玩家账号
	 */
	private String account;
	
	@JsonIgnore
	public String getApiUrl(String url) {
		StringBuffer sb = new StringBuffer(url).append(this.account).append("?");
		return sb.toString();
	}
}
