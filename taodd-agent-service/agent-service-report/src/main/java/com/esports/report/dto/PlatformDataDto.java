package com.esports.report.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PlatformDataDto {

    private BigDecimal profitTotalAmount = BigDecimal.ZERO;

    private BigDecimal betTotalAmount = BigDecimal.ZERO;

    private BigDecimal platformTotalAmount = BigDecimal.ZERO;

    private BigDecimal activityTotalAmount = BigDecimal.ZERO;

    private List<PlatformReportDto> contents;
}
