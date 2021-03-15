package com.esports.basic.web.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel(value = "agentPasswordVo", description = "修改支付密码对象")
public class AgentPasswordVo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "原始密码", name = "originalPwd", example = "123456", required = true, dataType = "String")
	private String originalPwd;
	
	@ApiModelProperty(value = "会员密码", name = "password", example = "123456", required = true, dataType = "String")
	private String password;
	
	@ApiModelProperty(value = "确认密码", name = "repeatPwd", example = "123456", required = true, dataType = "String")
	private String repeatPwd;
}
