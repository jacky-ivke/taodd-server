package com.esports.center.basic.dto;

import lombok.Data;

@Data
public class DepositTotalDto {

    /**
     * 今日存款总次数
     */
    private Integer depositTotalCount;

    /**
     * 10分钟之内存款申请单数
     */
    private Integer depositNum;

    /**
     * 10分钟之内存款申请审核成功单数
     */
    private Integer depositSuccessNum;

    /**
     * 10分钟之内存款申请审核失败单数
     */
    private Integer depositFailureNum;
}
