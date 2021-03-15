package com.esports.adjust.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "agentAdjustDto", description = "调线基本信息")
public class AgentAdjustDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "本月调线", name = "currentMonthAdjust", example = "5", required = true, dataType = "Integer")
    private Integer currentMonthAdjust = 0;

    @ApiModelProperty(value = "调线次数", name = "adjustTotal", example = "5", required = true, dataType = "Integer")
    private Integer adjustTotal = 0;

    @ApiModelProperty(value = "调线率", name = "rate", example = "5", required = true, dataType = "double")
    private BigDecimal rate = BigDecimal.ZERO;
}
