package com.esports.report.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 佣金详情
 */
@Data
@ApiModel(value = "commissionDetailDto", description = "佣金报表详情")
public class CommissionDetailDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "发放时间", name = "createTime", example = "2020-06-18 00:00:00", required = true, dataType = "String")
    private Timestamp createTime;

    @ApiModelProperty(value = "总盈亏", name = "profitAmount", example = "200.00", required = true, dataType = "double")
    private BigDecimal profitAmount=BigDecimal.ZERO;

    @ApiModelProperty(value = "API返佣", name = "apiTakeAmount", example = "200.00", required = true, dataType = "double")
    private BigDecimal apiTakeAmount=BigDecimal.ZERO;

    @ApiModelProperty(value = "优惠分担", name = "discountAmount", example = "0.00", required = true, dataType = "double")
    private BigDecimal discountAmount=BigDecimal.ZERO;

    @ApiModelProperty(value = "返水分担", name = "rakeAmount", example = "0.00", required = true, dataType = "double")
    private BigDecimal rakeAmount=BigDecimal.ZERO;

    @ApiModelProperty(value = "存款手续", name = "depositCharge", example = "0.00", required = true, dataType = "double")
    private BigDecimal depositCharge=BigDecimal.ZERO;

    @ApiModelProperty(value = "取款手续", name = "drawCharge", example = "0.00", required = true, dataType = "double")
    private BigDecimal drawCharge=BigDecimal.ZERO;

    @ApiModelProperty(value = "应付佣金", name = "commissionAmount", example = "0.00", required = true, dataType = "double")
    private BigDecimal commissionAmount=BigDecimal.ZERO;

    @ApiModelProperty(value = "实付佣金", name = "actualAmount", example = "0.00", required = true, dataType = "double")
    private BigDecimal actualAmount=BigDecimal.ZERO;

    @ApiModelProperty(value = "上期记账金额", name = "unsettledAmount", example = "0.00", required = true, dataType = "double")
    private BigDecimal unsettledAmount=BigDecimal.ZERO;

    @ApiModelProperty(value = "平台费用", name = "platformFee", example = "0.00", required = true, dataType = "double")
    private BigDecimal platformFee = BigDecimal.ZERO;
}
