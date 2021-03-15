package com.esports.interest.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class WealthDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 套餐ID
     */
    private Long packageId;

    /**
     * 周期
     */
    private Integer days;

    /**
     * 利率
     */
    private BigDecimal rate;

    /**
     * 理财标题
     */
    private String title;

}
