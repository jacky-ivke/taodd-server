package com.esports.adjust.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@ApiModel(value = "agentAdjustRecordsDto", description = "调线记录信息")
public class AgentAdjustRecordsDto implements Serializable {


    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "日期", name = "createTime", example = "2020-06-18 00:00:00", required = true, dataType = "String")
    private Timestamp createTime;

    @ApiModelProperty(value = "會員账号", name = "account", example = "agent118", required = true, dataType = "String")
    private String account;

    @ApiModelProperty(value = "调前存款", name = "depositAmount", example = "1000", required = true, dataType = "double")
    private BigDecimal depositAmount;

    @ApiModelProperty(value = "调前存款", name = "profitAmount", example = "100", required = true, dataType = "double")
    private BigDecimal profitAmount;

    @ApiModelProperty(value = "调时中心余额", name = "balance", example = "100", required = true, dataType = "double")
    private BigDecimal balance;

    @ApiModelProperty(value = "调时场馆余额", name = "apiBalance", example = "100", required = true, dataType = "double")
    private BigDecimal apiBalance;

    @ApiModelProperty(value = "调时场馆余额", name = "apiBalance", example = "100", required = true, dataType = "double")
    private BigDecimal firstDeposit;

    @ApiModelProperty(value = "状态", name = "okStatus", example = "0、处理中，1、成功， 2、拒绝", required = true, dataType = "int")
    private Integer okStatus;

    @ApiModelProperty(value = "备注", name = "remarks", example = "", required = true, dataType = "String")
    private String remarks;

}
 
