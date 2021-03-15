package com.esports.external.handler.xint.mg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 创建游戏账号
 * @author jacky
 *
 */
@Data
public class ReqCreatePlayerDTO {
	
	/**
	 * 代理賬號
	 */
	@JsonProperty("parent_id")
	private String partenId;

	/**
	 * 游戏账号
	 */
	@JsonProperty("username")
    private String userName;
    
    /**
     * 登录密码
     */
	@JsonProperty("password")
    private String password;
    
    /**
     * 会员外部唯一值
     */
	@JsonProperty("ext_ref")
    private String extRef;
}
