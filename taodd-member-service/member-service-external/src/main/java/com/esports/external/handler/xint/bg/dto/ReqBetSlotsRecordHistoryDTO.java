package com.esports.external.handler.xint.bg.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ReqBetSlotsRecordHistoryDTO {
	
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
	 * 开始时间
	 */
	private String startTime;
	
	/**
	 * 结束时间
	 */
	private String endTime;
	
	private Long pageIndex=1L;
	
	private Integer pageSize = 20;
	
	@JsonIgnore
    public String getMethod() {
    	return "open.order.bg.egame.query";
    }

}
