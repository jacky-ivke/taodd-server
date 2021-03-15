package com.esports.external.handler.xint.bg.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 *
 */
@Data
public class ReqGetBalanceDTO {

    private String random;
    
    private String digest;
    
    private String sn;
    
    private String loginId;
    
    @JsonIgnore
    public String getMethod() {
    	return "open.balance.get";
    }

}
