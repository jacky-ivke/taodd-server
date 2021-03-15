package com.esports.report.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgentCommissionDto {

    private Integer memNum;

    private BigDecimal profitTotalAmount;

    private String commissionCode;

}
