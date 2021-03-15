package com.esports.center.bankcard.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @ClassName: BankCard
 * @Description: 卡包管理
 * @Author: jacky
 * @Version: V1.0
 */
@Entity
@Table(name = "tb_bank_card", indexes = {@Index(name = "account_idx", columnList = "ACCOUNT")})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MemberBankCard implements Serializable {

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
    @Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 20, columnDefinition = "bigint(20)")
    private Long id;

    /**
     * 银行账号【卡号】
     */
    @Column(name = "BANK_ACCOUNT", unique = false, nullable = true, insertable = true, updatable = true, length = 255, columnDefinition = "varchar(255)")
    @ColumnTransformer(read = "convert(aes_decrypt(unhex(bank_account), 'aoying') using utf8)", write = "hex(aes_encrypt(?, 'aoying'))")
    private String bankAccount;

    /**
     * 真实姓名
     */
    @Column(name = "BANK_REALNAME", unique = false, nullable = true, insertable = true, updatable = true, length = 255, columnDefinition = "varchar(255)")
    @ColumnTransformer(read = "convert(aes_decrypt(unhex(bank_realname), 'aoying') using utf8)", write = "hex(aes_encrypt(?, 'aoying'))")
    private String bankRealName;

    /**
     * 渠道编号
     */
    @Column(name = "CHANNEL_CODE", unique = false, nullable = true, insertable = true, updatable = true, length = 16, columnDefinition = "varchar(16)")
    private String channelCode;

    /**
     * 银行名称
     */
    @Column(name = "BANK_NAME", unique = false, nullable = true, insertable = true, updatable = true, length = 255, columnDefinition = "varchar(255)")
    @ColumnTransformer(read = "convert(aes_decrypt(unhex(bank_name), 'aoying') using utf8)", write = "hex(aes_encrypt(?, 'aoying'))")
    private String bankName;

    /**
     * 银行卡类型
     */
    @Column(name = "BANK_TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 1,
            columnDefinition = "int(1)")
    private Integer bankType;

    /**
     * 开户网点
     */
    @Column(name = "BANK_DOT", unique = false, nullable = true, insertable = true, updatable = true, length = 128,
            columnDefinition = "varchar(128)")
    private String bankDot;

    /**
     * 开户省份
     */
    @Column(name = "PROVINCE", unique = false, nullable = true, insertable = true, updatable = true, length = 16,
            columnDefinition = "varchar(16)")
    private String province;

    /**
     * 开户城市
     */
    @Column(name = "CITY", unique = false, nullable = true, insertable = true, updatable = true, length = 16,
            columnDefinition = "varchar(16)")
    private String city;

    /**
     * 状态
     */
    @Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1,
            columnDefinition = "int(1)")
    private Integer okStatus;

    /**
     * 绑定时间
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true,
            columnDefinition = "datetime")
    private Timestamp createTime;

    /**
     * 玩家账号【会员账号、代理账号】
     */
    @Column(name = "ACCOUNT", unique = false, nullable = true, insertable = true, updatable = true, length = 20,
            columnDefinition = "varchar(20)")
    private String account;
}
