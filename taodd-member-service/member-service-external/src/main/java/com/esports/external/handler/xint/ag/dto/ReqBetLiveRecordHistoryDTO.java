package com.esports.external.handler.xint.ag.dto;

import com.esports.utils.Md5Utils;
import lombok.Data;

@Data
public class ReqBetLiveRecordHistoryDTO{
	
	/**
	 * 代理编号
	 */
	private String cagent = "HU7";

	/**
	 * 开始时间
	 */
	private String startdate;
	
	/**
	 * 结束时间,每次请求时间只能在 10分钟以内的数据
	 */
	private String enddate;
	
	/**
	 * 分页
	 */
	private Long page;
	
	/**
	 * 分页参数
	 */
	private Integer perpage;
	
	public String getKey() {
		String key = this.cagent+this.startdate+this.enddate+this.page+this.perpage+"5B4FF457C74F8372C73D6A50DED03508";
		String sign = Md5Utils.getMD5(key).toLowerCase();
		return sign;
	}
}
