package com.esports.external.handler.xint.bg.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ReqBetSportsRecordHistoryDTO {

	/**
	 * 随机字符串
	 */
	private String random;
	
	/**
	 * 签名摘要sign=md5(random+sn+secretKey)
	 */
	private String sign;
	
	/**
	 * 厅代码
	 */
	private String sn;
	
	/**
	 * 订单编号
	 */
	private String orderNo;
	
	/**
	 * 玩家登录ID列表
	 */
	private List<String> loginId = Collections.emptyList();
	
	/**
	 * 代理ID
	 */
	private Long agentId;
	
	/**
	 * 代理账号
	 */
	private String agentLoginId = "";
	
	/**
	 * 时间类型  1: 投注时间(默认); 2: 结算时间
	 */
	private Integer timeType = 1;
	
	/**
	 * 竞彩注单状态
	 */
	private Integer status;
	
	/**
	 * 游戏类型，1: 竞足;  2: 竞篮，不传查询所有
	 */
	private Integer gameType;
	
	/**
	 * 时区，指定开始和结束时间时有效1: 北京时间; 2: 美东时间(默认)
	 */
	private Integer timeZone=1;
	
	/**
	 * 开始时间
	 */
	private String startTime;
	
	/**
	 * 结束时间
	 */
	private String endTime;
	
	/**
	 * 页索引，默认1
	 */
	private Long pageIndex;
	
	private Integer pageSize = 20;
	
	/**
	 * 99 ：表示精简返回，不返回parlayResult,betContent
	 */
	private String leanback = "";
	
	@JsonIgnore
    public String getMethod() {
    	return "open.order.bg.sportlottery.query";
    }
}
