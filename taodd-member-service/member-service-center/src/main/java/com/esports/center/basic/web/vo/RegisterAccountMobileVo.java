package com.esports.center.basic.web.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel(value = "registerAccountMobileVo", description = "账号手机号码注册")
public class RegisterAccountMobileVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "会员账号", name = "account", example = "jacky1234", required = false, dataType = "String")
    private String account;

    @ApiModelProperty(value = "会员密码", name = "password", example = "123456", required = true, dataType = "String")
    private String password;

    @ApiModelProperty(value = "确认密码", name = "password", example = "123456", required = true, dataType = "String")
    private String confirmPassword;

    @ApiModelProperty(value = "手机号码", name = "mobile", example = "15100090909", required = true, dataType = "String")
    private String mobile;

    @ApiModelProperty(value = "验证码", name = "code", example = "123456", required = true, dataType = "String")
    private String code;

    @ApiModelProperty(value = "邀请码", name = "inviteCode", example = "pqtBRhi5", required = false, dataType = "String")
    private String inviteCode;

    @ApiModelProperty(value = "注册来源", name = "source", example = "0", required = true, dataType = "String")
    private Integer source;

    @ApiModelProperty(value = "代理推广", name = "iCode", example = "1843625", required = false, dataType = "String")
    private String iCode;
}
