package com.esports.external.handler.xint.pt.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 *
 */
@Data
public class ReqTransferDTO{

	/**
	 * Player username，Prefix included
	 */
    private String playername;
    
    /**
     * Deposit amount
     */
    private BigDecimal amount;
    
    /**
     * Admin name
     */
    private String adminname;
    
    /**
     * External transaction ID
     */
    private String externaltranid;
    
    /**
     * 1|0 即：是否强制提款
     */
    private String isForce = "1";
    
    private String losebonus="";
}
