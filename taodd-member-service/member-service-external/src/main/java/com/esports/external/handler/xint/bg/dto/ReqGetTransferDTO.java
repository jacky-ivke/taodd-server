package com.esports.external.handler.xint.bg.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ReqGetTransferDTO{
	
	private String random;
	    
    private String sign;
    
    private String sn;
    
    private String loginId;
    
    private String agentLoginId;
	    
    private String bizId;
    
    @JsonIgnore
    public String getMethod() {
    	return "open.balance.transfer.query";
    }
}

