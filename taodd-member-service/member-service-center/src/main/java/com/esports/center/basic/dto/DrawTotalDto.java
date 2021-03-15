package com.esports.center.basic.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DrawTotalDto {

    private Integer drawCount;

    private BigDecimal drawAmount;
}
