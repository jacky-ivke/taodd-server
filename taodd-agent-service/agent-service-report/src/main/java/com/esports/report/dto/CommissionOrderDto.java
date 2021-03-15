package com.esports.report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CommissionOrderDto implements Serializable {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;


    private Integer okStatus;

    /**
     * 结算月份
     */
    private String month;

    /**
     * 结算批次号
     */
    private String serialNo;

    /**
     * 订单编号【可以用来区分一个批次号里面，代理返佣次数，方便数据查看】
     */
    private String orderNo;

    /**
     * 代理账号
     */
    private String account;

    /**
     * 活跃数
     */
    private Integer memNum;

    /**
     * API分成
     */
    private BigDecimal apiTakeAmount;

    /**
     * 本期投注盈亏总额
     */
    private BigDecimal profitAmount;

    /**
     * 本期有效投注
     */
    private BigDecimal betValidAmount;

    /**
     * 本期投注总额
     */
    private BigDecimal betTotal;

    /**
     * 本期返水承担
     */
    private BigDecimal rakebacAmount;

    /**
     * 本期优惠承担
     */
    private BigDecimal discountAmount;

    /**
     * 本期存款费用承担
     */
    private BigDecimal depositAmount;

    /**
     * 本期取款费用承担
     */
    private BigDecimal drawAmount;

    /**
     * 本期其它承担
     */
    private BigDecimal otherAmount;

    /**
     * 本期应付返佣金额
     */
    private BigDecimal commissionAmount;

    /**
     * 本期实付返佣金额
     */
    private BigDecimal actualAmount;

    /**
     * 返佣发放时间
     */
    private Timestamp createTime;

    /**
     * 上期挂账金额
     */
    private BigDecimal unsettledAmount;

    /**
     * 平台费
     */
    private BigDecimal platformFee;


    /**
     * 请求IP
     */
    private Long ip;

    /**
     * 备注
     */
    private String remarks;
}
