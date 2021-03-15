package com.esports.patner.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class FriendDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 注册时间
	 */
	private Timestamp regTime;
	
	/**
	 * 会员账号
	 */
	private String account;
	
	/**
	 * 最后登录时间
	 */
	private Timestamp lastLoginTime;
	
}
