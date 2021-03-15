package com.esports.external.handler.xint.pt.dto;

import lombok.Data;

/**
 * 创建游戏账号
 * @author jacky
 *
 */
@Data
public class ReqCreatePlayerDTO {

    private String playername;
    
    private String adminname;
    
    private String entityname;
    
    private String password;
    
    /**
     * Unique code for player, usually set it same as Prefix
     */
    private String custom02;
}
