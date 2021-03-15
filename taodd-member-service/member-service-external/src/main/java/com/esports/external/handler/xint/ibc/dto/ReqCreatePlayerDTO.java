package com.esports.external.handler.xint.ibc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建游戏账号
 * @author jacky
 *
 */
@Data
public class ReqCreatePlayerDTO {

	/**
	 * 厂商标识符
	 */
	@JsonProperty("vendor_id")
    private String vendorId ;
    
    /**
     * 会员账号
     */
	@JsonProperty("vendor_member_id")
    private String memberId;
    
	/**
	 * 厂商 ID。此 ID 为厂商自行定义
	 */
	@JsonProperty("operatorId")
    private String operatorId = "AOYING_ZHP";
    
	/**
	 * 会员姓氏
	 */
	@JsonProperty("firstname")
    private String firstName;
    
	/**
	 * 会员名字
	 */
	@JsonProperty("lastname")
    private String lastName="";
    
	/**
	 * 会员登入名称
	 */
	@JsonProperty("username")
    private String userName="";
    
    /**
     * 赔率  (1、马来盘、2、中国盘、3、欧洲盘等  4、印度尼西亚盘  5、美国盘)
     */
	@JsonProperty("oddstype")
    private String oddsType = "2";
    
	/**
	 * 币别 13、人名币
	 */
	@JsonProperty("currency")
    private Integer currency = 13;
    
	/**
	 * 会员单笔最大限制转账金额
	 */
	@JsonProperty("maxtransfer")
    private BigDecimal maxTransfer;
    
	/**
	 * 会员单笔最小限制转账金额最小限制转账金额必须大于等于1
	 */
	@JsonProperty("mintransfer")
    private BigDecimal minTransfer;
    
	@JsonProperty("custominfo1")
    private String custominfo1="";
    
	@JsonProperty("custominfo2")
    private String custominfo2="";
    
	@JsonProperty("custominfo3")
    private String custominfo3="";
    
	@JsonProperty("custominfo4")
    private String custominfo4="";
    
	@JsonProperty("custominfo5")
    private String custominfo5="";
}
