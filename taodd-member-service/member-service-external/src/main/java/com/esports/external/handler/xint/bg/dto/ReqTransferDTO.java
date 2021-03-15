package com.esports.external.handler.xint.bg.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 *
 */
@Data
public class ReqTransferDTO{

	/**
	 * 随机字符串
	 */
    private String random;
    
    /**
     * 签名
     */
    private String digest;
    
    /**
     * 厅代码
     */
    private String sn;
    
    /**
     * 用户登录ID
     */
    private String loginId;
    
    /**
     * 转账金额(负数表示从BG 转出，正数转入)，支持2位小数,切记必须是字符串
     */
    private String amount;
    
    /**
     * 转账业务ID，由调用方提供
     */
    private String bizId;
    
    /**
     * 是否检查转账业务ID的唯一性. 1: 检查; 0: 不检查(默认)
     */
    private String checkBizId = "1";
    
    @JsonIgnore
    public String getMethod() {
    	return "open.balance.transfer";
    }
}
