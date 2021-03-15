package com.esports.report.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PlatformReportDto implements Serializable {

    /**
     * 平台编号
     */
    private String apiCode;

    /**
     * 游戏类型
     */
    private String gameType;

    /**
     * 平台费
     */
    private BigDecimal platformFee;

    /**
     * 平台费率百分比
     */
    private BigDecimal percentage;

    /**
     * 有效投注
     */
    private BigDecimal betAmount;

    /**
     * 输赢
     */
    private BigDecimal profitAmount;
}
