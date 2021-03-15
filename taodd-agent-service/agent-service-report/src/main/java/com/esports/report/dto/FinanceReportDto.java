package com.esports.report.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "financeReportDto", description = "财务报表数据对象")
public class FinanceReportDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "存款", name = "depositAmount", example = "150.00", required = true, dataType = "double")
    private BigDecimal depositAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "取款", name = "drawAmount", example = "150.00", required = true, dataType = "double")
    private BigDecimal drawAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "优惠", name = "discountAmount", example = "150.00", required = true, dataType = "double")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "返水", name = "rakeAmount", example = "150.00", required = true, dataType = "double")
    private BigDecimal rakeAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "总输赢", name = "winTotalAmount", example = "150.00", required = true, dataType = "double")
    private BigDecimal winTotalAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "净输赢", name = "netWinAmount", example = "150.00", required = true, dataType = "double")
    private BigDecimal netWinAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "平台费", name = "platformFee", example = "150.00", required = true, dataType = "double")
    private BigDecimal platformFee = BigDecimal.ZERO;

}
