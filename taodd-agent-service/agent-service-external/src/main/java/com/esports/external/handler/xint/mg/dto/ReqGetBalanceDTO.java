package com.esports.external.handler.xint.mg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 */
@Data
public class ReqGetBalanceDTO {
	
	/**
	 * 欲查询之钱包隶属之会员 id，请提供 account_id 或是account_ext_ref 其中一项
	 */
	@JsonProperty("account_ext_ref")
	private String loginName="";
    
}
