package com.esports.center.basic.web.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel(value = "passwordVo", description = "修改密码对象")
public class PasswordVo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "会员密码", name = "password", example = "123456", required = true, dataType = "String")
    private String password;
    
    @ApiModelProperty(value = "验证码", name = "verifyCode", example = "123456", required = true, dataType = "String")
    private String verifyCode;
    
    @ApiModelProperty(value = "手机号码", name = "mobile", example = "15100090909", required = true, dataType = "String")
    private String mobile;
}
