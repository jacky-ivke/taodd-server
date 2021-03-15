package com.esports.external.handler.xint.leg.dto;

import lombok.Data;

/**
 *
 */
@Data
public class ReqGetBalanceDTO {

	/**
	 * 玩家账号
	 */
    private String s;
    
    /**
     * 账号密码
     */
    private String account;
}
