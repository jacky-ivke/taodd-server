package com.esports.report.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 佣金报表
 */
@Data
@ApiModel(value = "commissionReportDeto", description = "佣金报表")
public class CommissionReportDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "当月佣金", name = "commissionAmount", example = "400.00", required = false, dataType = "double")
    private BigDecimal commissionAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "活跃人数", name = "activeMember", example = "4", required = true, dataType = "Integer")
    private Integer activeMember = 0;

    @ApiModelProperty(value = "总盈亏", name = "profitAmount", example = "150.00", required = true, dataType = "double")
    private BigDecimal profitAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "平台费", name = "platformFee", example = "150.00", required = true, dataType = "double")
    private BigDecimal platformFee = BigDecimal.ZERO;
}
