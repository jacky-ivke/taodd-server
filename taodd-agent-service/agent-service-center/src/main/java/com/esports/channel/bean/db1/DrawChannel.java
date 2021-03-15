package com.esports.channel.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_draw_channel")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DrawChannel implements Serializable {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 1L;

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
     * 渠道名称
     */
    @Column(name = "CHANNEL_NAME", unique = false, nullable = true, insertable = true, updatable = true, length = 50, columnDefinition = "varchar(50)")
    private String channelName;

    /**
     * 渠道编号
     */
    @Column(name = "CHANNEL_CODE", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
    private String channelCode;

    /**
     * 优先级
     */
    @Column(name = "PRIORITY", unique = false, nullable = true, insertable = true, updatable = true, length = 4, columnDefinition = "int(4)")
    private Integer priority;

    /**
     * 图标
     */
    @Column(name = "ICON", unique = false, nullable = true, insertable = true, updatable = true, length = 255, columnDefinition = "varchar(255)")
    private String icon;

    /**
     * LOGO
     */
    @Column(name = "LOGO", unique = false, nullable = true, insertable = true, updatable = true, length = 255, columnDefinition = "varchar(255)")
    private String logo;

    /**
     * 更新时间
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp createTime;


    /**
     * 备注说明
     */
    @Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200, columnDefinition = "varchar(200)")
    private String remarks;
}
