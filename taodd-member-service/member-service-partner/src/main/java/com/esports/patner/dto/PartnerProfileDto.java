package com.esports.patner.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 超级合伙人，个人资料信息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PartnerProfileDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 推荐佣金
	 */
	private BigDecimal inviteBalance = BigDecimal.ZERO;
	
	/**
	 * 推荐佣金总额
	 */
	private BigDecimal inviteTotalBalance = BigDecimal.ZERO;
	
	/**
	 * 有效好友数
	 */
	private Integer friends = 0;
	
	/**
	 * 好友投注总额
	 */
	private BigDecimal friendsBetTotaAmount = BigDecimal.ZERO;
	
	/**
	 * 推荐码
	 */
	private String inviteCode="";
	
	/**
	 * 专属链接
	 */
	private String h5exclusiveLinks="";
	
	/**
     * 专属链接
     */
	private String pcexclusiveLinks="";
	
	/**
	 * 一级好友投注
	 */
	private BigDecimal firstBetTotalAmount = BigDecimal.ZERO;
	
	/**
	 * 二级好友投注
	 */
	private BigDecimal secondBetTotalAmount = BigDecimal.ZERO;
	
	/**
	 * 三级好友投注
	 */
	private BigDecimal thirdBetTotalAmount = BigDecimal.ZERO;
	
}
