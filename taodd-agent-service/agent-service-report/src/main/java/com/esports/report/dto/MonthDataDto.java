package com.esports.report.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 获取本月数据
 */
@Data
@ApiModel(value = "monthDataDto", description = "本月数据对象")
public class MonthDataDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "新注册人数", name = "registerMember", example = "150", required = true, dataType = "Integer")
    private Integer registerMember = 0;

    @ApiModelProperty(value = "首存人数", name = "firstDepositMember", example = "4", required = true, dataType = "Integer")
    private Integer firstDepositMember = 0;

    @ApiModelProperty(value = "活跃人数", name = "activeMember", example = "4", required = true, dataType = "Integer")
    private Integer activeMember = 0;

    @ApiModelProperty(value = "净输赢", name = "netWinAmount", example = "4", required = true, dataType = "double")
    private BigDecimal netWinAmount = BigDecimal.ZERO;
}
