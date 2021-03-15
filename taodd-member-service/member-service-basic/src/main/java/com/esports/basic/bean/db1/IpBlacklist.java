package com.esports.basic.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_ip_blacklist")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class IpBlacklist implements Serializable {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "paymentableGenerator", strategy = "native")
    @Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 20, columnDefinition = "bigint(20)")
    private Long id;

    /**
     * 开始IP段
     */
    @Column(name = "IP_FROM", unique = false, nullable = true, insertable = true, updatable = true, length = 11, columnDefinition = "bigint(20)")
    private Long ipFrom;

    /**
     * 结束IP段
     */
    @Column(name = "IP_TO", unique = false, nullable = true, insertable = true, updatable = true, length = 11, columnDefinition = "bigint(20)")
    private Long ipTo;

    /**
     * 开始IP地址
     */
    @Column(name = "IP_START", unique = false, nullable = true, insertable = true, updatable = true, length = 15, columnDefinition = "varchar(15)")
    private String ipStart;

    /**
     * 结束IP地址
     */
    @Column(name = "IP_END", unique = false, nullable = true, insertable = true, updatable = true, length = 15, columnDefinition = "varchar(15)")
    private String ipEnd;

    /**
     * 备注
     */
    @Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200, columnDefinition = "varchar(200)")
    private String remarks;

    /**
     * 更新时间
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp createTime;

    /**
     * 状态
     */
    @Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer okStatus;
}
