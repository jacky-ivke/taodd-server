package com.esports.external.handler.yabo.slots.dto;

import lombok.Data;

/**
 *
 */
@Data
public class ReqGetBalanceDTO {

	/**
	 * 玩家账号
	 */
    private String memberId;
    
    /**
     * 账号密码
     */
    private String memberPwd;

    /**
     * 玩家IP
     */
    private String memberIp;
}
