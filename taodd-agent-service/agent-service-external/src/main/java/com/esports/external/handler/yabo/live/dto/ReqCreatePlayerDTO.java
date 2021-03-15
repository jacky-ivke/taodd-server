package com.esports.external.handler.yabo.live.dto;

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
    private String loginName;
    
    /**
     * 登录密码
     */
    private String loginPassword;
    
    /**
     * 昵称
     */
    private String nickName;
    
    /**
     * 语言
     */
    private Integer lang;
    
    /**
     * 时间戳
     */
    private Long timestamp;
}
