package com.esports.center.scheme.bean.db1;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_deposit_scheme")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DepositScheme implements Serializable {

    /**
     * 主键ID,自增长
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "paymentableGenerator", strategy = "native")
    @Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 11, columnDefinition = "bigint(20)")
    private Long id;

    /**
     * 状态
     */
    @Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer okStatus;

    /**
     * 单日存款申请总数
     */
    @Column(name = "DEPOSIT_TOTAL_NUM", unique = false, nullable = true, insertable = true, updatable = true, length = 4,columnDefinition="int(4)")
    private Integer depositTotalNum;

    /**
     * 10分钟类允许存款申请次数
     */
    @Column(name = "DEPOSIT_NUM", unique = false, nullable = true, insertable = true, updatable = true, length = 4,columnDefinition="int(4)")
    private Integer depositNum;

    /**
     * 10分钟之内存款次数耗尽，失败率预警值
     */
    @Column(name = "DEPOSIT_FAILURE_RATE",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
    private BigDecimal depositFailureRate = BigDecimal.ZERO;
}
