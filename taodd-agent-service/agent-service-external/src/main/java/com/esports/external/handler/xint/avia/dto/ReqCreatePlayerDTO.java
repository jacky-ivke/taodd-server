package com.esports.external.handler.xint.avia.dto;

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
	 * 游戏账号
	 */
	@JsonProperty("UserName")
    private String userName;
    
    /**
     * 登录密码
     */
	@JsonProperty("Password")
    private String password;
    
    /**
     * 可选。 指定会员的币种，如果留空则为商户的开户默认币种。 请注意，会员币种为注册时设定，之后不可再修改。
     */
	@JsonProperty("Currency")
    private String currency;
}
