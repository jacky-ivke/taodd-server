package com.esports.center.basic.dto;

import com.esports.constant.GlobalCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员个人信息资料
 *
 * @author jacky
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel(value = "memberProfileDto", description = "会员个人资料对象")
public class MemberProfileDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "真实姓名", name = "realName", example = "张飞", required = false, dataType = "String")
    private String realName = "";

    @ApiModelProperty(value = "登录账号", name = "account", example = "zhangfei", required = false, dataType = "String")
    private String account = "";

    @ApiModelProperty(value = "手机号", name = "mobile", example = "1510000000", required = false, dataType = "String")
    private String mobile = "";

    @ApiModelProperty(value = "电子邮箱", name = "email", example = "123456789@153.com", required = true, dataType = "String")
    private String email = "";

    @ApiModelProperty(value = "出生日期", name = "birthday", example = "2020-01-01", required = false, dataType = "String")
    private String birthday = "";

    @ApiModelProperty(value = "微信", name = "wechat", example = "1510000000", required = false, dataType = "String")
    private String wechat = "";

    @ApiModelProperty(value = "QQ", name = "qq", example = "123456788", required = false, dataType = "String")
    private String qq = "";

    @ApiModelProperty(value = "当前层级", name = "gradeCode", example = "L1", required = false, dataType = "String")
    private String gradeCode = GlobalCode._DEFAULT_GRADE.getMessage();

    @ApiModelProperty(value = "vip等级", name = "vip", example = "1", required = false, dataType = "Integer")
    private Integer vip;

    @ApiModelProperty(value = "爵位", name = "title", example = "平民", required = false, dataType = "String")
    private String title = "";

    @ApiModelProperty(value = "账户余额", name = "title", example = "0.00", required = false, dataType = "BigDecimal")
    private BigDecimal balance = BigDecimal.ZERO;

    @ApiModelProperty(value = "头像", name = "headPortrait", example = "http://xxxxxxxxxxxxx.png", required = false, dataType = "String")
    private String headPortrait = "";

    @ApiModelProperty(value = "是否设置交易密码", name = "isCompleteTradePwd", example = "true/false", required = true, dataType = "boolean")
    private Boolean isCompleteTradePwd;

    @ApiModelProperty(value = "登录区域", name = "city", example = "联合国", required = false, dataType = "String")
    private String city = "";

    @ApiModelProperty(value = "登录ip", name = "ip", example = "0.0.0.0", required = false, dataType = "String")
    private String ip = "";
}
