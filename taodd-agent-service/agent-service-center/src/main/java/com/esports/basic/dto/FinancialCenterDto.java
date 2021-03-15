package com.esports.basic.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel(value = "financialCenterDto", description = "代理财务中心对象")
@Data
public class FinancialCenterDto implements Serializable {

    private BigDecimal commissionBalance = BigDecimal.ZERO;

    private BigDecimal otherBalance = BigDecimal.ZERO;

    private BigDecimal drawPendingAmount = BigDecimal.ZERO;

}
