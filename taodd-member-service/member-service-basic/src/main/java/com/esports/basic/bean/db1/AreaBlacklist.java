 package com.esports.safety.bean.db1;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.esports.framework.core.crud.bean.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * 
* @ClassName: AreaBlacklist 
* @Description: 站点区域限制（城市Id|国家|区域|省份|城市|ISP）
* @Author: jacky
* @Version: V1.0
 */
@Entity
@Table(name = "tb_area_blacklist")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AreaBlacklist implements BaseEntity{

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
     @Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 11,columnDefinition="bigint(20)")
     private Long id;
     
     /**
      * 国家
      */
     @Column(name = "COUNTRY", unique = false, nullable = true, insertable = true, updatable = true, length = 16,columnDefinition="varchar(16)")
     private String country;
     
     /**
      * 区域
      */
     @Column(name = "REGION", unique = false, nullable = true, insertable = true, updatable = true, length = 16,columnDefinition="varchar(16)")
     private String region;
     
     /**
      * 省份
      */
     @Column(name = "PROVINCE", unique = false, nullable = true, insertable = true, updatable = true, length = 16,columnDefinition="varchar(16)")
     private String province; 
     
     /**
      * 城市
      */
     @Column(name = "CITY", unique = false, nullable = true, insertable = true, updatable = true, length = 16,columnDefinition="varchar(16)")
     private String city; 
     
     /**
      * 运营商
      */
     @Column(name = "ISP", unique = false, nullable = true, insertable = true, updatable = true, length = 16,columnDefinition="varchar(16)")
     private String isp;
     
     /**
      * 限制区域
      */
     @Column(name = "LIMIT_AREA", unique = false, nullable = true, insertable = true, updatable = true, length = 200,columnDefinition="varchar(128)")
     private String limitArea;
     
     /**
      * 备注
      */
     @Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200,columnDefinition="varchar(200)")
     private String remarks;
     
     /**
      * 更新时间
      */
     @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true,columnDefinition="datetime")
     private Timestamp createTime;
     
     /**
      * 状态
      */
     @Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1,columnDefinition="int(1)")
     private Integer okStatus;
    
}
