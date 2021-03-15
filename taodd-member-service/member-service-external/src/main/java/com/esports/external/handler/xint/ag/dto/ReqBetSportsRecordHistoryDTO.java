package com.esports.external.handler.xint.ag.dto;

import com.esports.utils.Md5Utils;
import lombok.Data;

@Data
public class ReqBetSportsRecordHistoryDTO{
	
	private String cagent;

	/**
	 * 开始时间
	 */
	private String startdate;
	
	/**
	 * 结束时间
	 */
	private String enddate;
	
	private Long page;
	
	private Integer perpage;
	
	public String getKey() {
		String key = this.cagent+this.startdate+this.enddate+this.page+this.perpage+"5B4FF457C74F8372C73D6A50DED03508";
		String sign = Md5Utils.getMD5(key).toLowerCase();
		return sign;
	}
}
