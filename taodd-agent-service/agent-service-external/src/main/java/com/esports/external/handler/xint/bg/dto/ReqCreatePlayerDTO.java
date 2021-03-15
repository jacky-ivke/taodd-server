package com.esports.external.handler.xint.bg.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 创建游戏账号
 * @author jacky
 *
 */
@Data
public class ReqCreatePlayerDTO {

	/**
	 * 随机字符串，建议使用UUID
	 */
    private String random;
    
    /**
     * 簽名
     */
    private String digest;
    
    /**
     * 厅代码
     */
    private String sn;
    
    /**
     * 登录ID
     */
    private String loginId="";
    
    /**
     * 会员昵称
     */
    private String nickname="";
    
    /**
     * 代理商账号，将会员账号创建到指定的代理名下
     */
    private String agentLoginId;
    
    /**
     * 客户端的真实IP地址
     */
    private String fromIp="";
    
    @JsonIgnore
    public String getMethod() {
    	return "open.user.create";
    }
}
