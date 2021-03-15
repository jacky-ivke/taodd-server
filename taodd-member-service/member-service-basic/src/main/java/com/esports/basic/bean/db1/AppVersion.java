package com.esports.basic.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_app_version")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class AppVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "paymentableGenerator", strategy = "native")
    @Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 20, columnDefinition = "bigint(20)")
    private Long id;

    /**
     * 版本描述
     */
    @Column(name = "APP_DESC", unique = false, nullable = true, insertable = true, updatable = true, length = 300, columnDefinition = "varchar(300)")
    private String describe;

    /**
     * 版本号
     */
    @Column(name = "APP_VERSION", unique = false, nullable = true, insertable = true, updatable = true, length = 32, columnDefinition = "varchar(32)")
    private String version;

    /**
     * 下载地址
     */
    @Column(name = "DOWNLOAD", unique = false, nullable = true, insertable = true, updatable = true, length = 255, columnDefinition = "varchar(255)")
    private String download;

    /**
     * 版本大小
     */
    @Column(name = "FILE_SIZE", unique = false, nullable = true, insertable = true, updatable = true, length = 12, columnDefinition = "decimal(12,2)")
    private BigDecimal fileSize;

    /**
     * 包名
     */
    @Column(name = "PAKAGE_NAME", unique = false, nullable = true, insertable = true, updatable = true, length = 128, columnDefinition = "varchar(128)")
    private String pakageName;

    /**
     * 状态
     */
    @Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer okStatus = 1;

    /**
     * APK类型
     */
    @Column(name = "APK_TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 32, columnDefinition = "varchar(32)")
    private String apkType;


    /**
     * 升级方式(0、可选 1、必须)
     */
    @Column(name = "UPGRADE_MODE", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer upgradeMode = 1;

    /**
     * 更新时间
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp createTime;

}
