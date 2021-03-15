package com.esports.bankcard.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BankCardDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 银行卡号
     */
    private String bankAccount;

    /**
     * 实名认证
     */
    private String bankRealName;

    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 银行卡类型
     */
    private Integer bankType;

    /**
     * 图标
     */
    private String icon;
}
