package com.esports.external.handler.xint.ag.dto;

import lombok.Data;

/**
 *
 */
@Data
public class ReqGetTransferDTO {

	/**
	 * 代理编码
	 */
	private String cagent;
	
	/**
	 * 序列号
	 */
	private String billno;
	
	
	private String method = "qos";
	
	private Integer actype = 1;
	
	private String cur = "CNY";

}
