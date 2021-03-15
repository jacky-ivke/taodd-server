package com.esports.external.handler.yabo.lottery.dto;

import lombok.Data;

@Data
public class ReqGetTransferDTO{
	
	/**
	 * 查询开始时间
	 */
	private String startTime="";
	
	
	/**
	 * 查询的结束时间
	 */
	private String endTime="";
	
	/**
	 *玩家账号ID
	 */
	private String member;

	/**
	 * 商户账号
	 */
    private String merchant;

	/**
	 * 讯息号
	 */
    private String notifyId;
    
    private String tradeType="";
    
    private String pageSize="";
    
    private String pageNum="";
    
    /**
     * 时间戳
     */
    private Long timestamp;
}

