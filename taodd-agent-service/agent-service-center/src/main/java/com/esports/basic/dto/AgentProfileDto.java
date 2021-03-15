package com.esports.basic.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "agentProfileDto", description = "代理个人资料对象")
public class AgentProfileDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "代理账号", name = "account", example = "agent118", required = true, dataType = "String")
    private String account;

    @ApiModelProperty(value = "代理姓名", name = "realName", example = "张**", required = true, dataType = "String")
    private String realName;

    @ApiModelProperty(value = "手机号", name = "mobile", example = "张**", required = true, dataType = "String")
    private String mobile;

    @ApiModelProperty(value = "是否设置交易密码", name = "isCompleteTradePwd", example = "true/false", required = true, dataType = "boolean")
    private Boolean isCompleteTradePwd;

    @ApiModelProperty(value = "邮箱", name = "email", example = "621700912@qq.com", required = true, dataType = "String")
    private String email;

    @ApiModelProperty(value = "下线用户", name = "subMember", example = "150", required = true, dataType = "int")
    private Integer subMember;
    
    @ApiModelProperty(value = "合营代码", name = "inviteCode", example = "150", required = true, dataType = "String")
    private String inviteCode;

    @ApiModelProperty(value = "登录区域", name = "city", example = "联合国", required = false, dataType = "String")
    private String city;

    @ApiModelProperty(value = "登录ip", name = "ip", example = "0.0.0.0", required = false, dataType = "String")
    private String ip;
}
