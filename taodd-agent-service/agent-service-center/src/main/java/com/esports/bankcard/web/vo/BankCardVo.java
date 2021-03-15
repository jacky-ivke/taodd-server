package com.esports.bankcard.web.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BankCardVo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String bankAccount;
	
	private String bankRealName;
	
	private String bankName;
	
	private Integer bankType;
	
	private String bankDot;
	
	private String province;
	
	private String city;
}
