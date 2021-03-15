package com.esports.external.handler.yabo.lottery.dto;

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
    private String member;
    
    /**
     * 登录密码
     */
    private Integer memberType;
    
    /**
     * 会员密码
     */
    private String password;
    
    /**
     * 商户编号
     */
    private String merchant;
    
    /**
     * 我方目前是直客模式,此参数不设置
     */
    private String doubleList="";
    
    /**
     * 我方目前是直客模式,此参数不设置
     */
    private String normalList="";
    
    /**
     * 时间戳
     */
    private Long timestamp;
}
