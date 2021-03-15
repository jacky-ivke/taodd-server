package com.esports.external.handler.xint.mg.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.sf.json.JSONObject;

@Data
public class ReqCreateDepositLoginPlayerDTO {
	
	/**
	 * 会员的外部参照值
	 */
	@JsonProperty("ext_ref")
    private String loginName;
	
	/**
	 * 欲开启之游戏 id
	 */
	@JsonProperty("item_id")
	private String gameId="";
	
	/**
	 * 欲产生的游戏连结之应用id
	 */
	@JsonProperty("app_id")
	private String appId="";
	
	@JsonProperty("login_context")
	private JSONObject loginContext = new JSONObject();
	
	@JsonProperty("conf_params")
	private JSONObject confParams = new JSONObject();
	
	@JsonIgnore
	public void setLang(String language) {
		this.loginContext.put("lang", language);
	}
	
	@JsonIgnore
	public void setTitanium(String titanium) {
		this.confParams.put("titanium", titanium);
	}
	
}
