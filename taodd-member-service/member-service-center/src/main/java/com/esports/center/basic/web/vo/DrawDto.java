package com.esports.center.basic.web.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel(value = "drawDto", description = "取款申请对象")
public class DrawDto implements Serializable{

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "取款人", name = "bankRealName", example = "张飞", required = false, dataType = "String")
	private String bankRealName;
	
	@ApiModelProperty(value = "银行名称", name = "bankName", example = "中国工商银行", required = false, dataType = "String")
	private String bankName;
	
	@ApiModelProperty(value = "银行账号", name = "bankAccount", example = "6217009889888876", required = false, dataType = "String")
	private String bankAccount;
	
	@ApiModelProperty(value = "取款金额", name = "amount", example = "100", required = false, dataType = "Integer")
	private BigDecimal amount;
	
	@ApiModelProperty(value = "订单来源", name = "source", example = "0、PC 1、H5", required = false, dataType = "Integer")
	private Integer source;
	
	@ApiModelProperty(value = "交易密码", name = "password", example = "123456", required = false, dataType = "Integer")
	private String password;
}
