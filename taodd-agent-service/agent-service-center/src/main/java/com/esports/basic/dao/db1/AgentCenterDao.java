package com.esports.basic.dao.db1;

import com.esports.basic.bean.db1.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AgentCenterDao extends JpaRepository<Agent, Long>, JpaSpecificationExecutor<Agent> {


    /**
     * 根据邮箱查询会员信息
     */
    Agent findByEmail(String email);

    /**
     * 根据账号查询会员信息
     */
    Agent findByAccount(String account);

    /**
     * 根据手机号查询会员信息
     */
    Agent findByMobile(String mobile);

    /**
     * 获取代理下线用户总数
     *
     * @param account
     * @return
     */
    @Query(value = "select count(t.id) from tb_player t where t.type=1 and t.leader=?1", nativeQuery = true)
    Integer getTotalSubMember(String account);


    /**
     * 获取当月代理下线新增总数
     */
    @Query(nativeQuery = true, value = "select count(t.id) from tb_player t where t.type=1 and t.leader=?1 and date_format(t.reg_time,'%Y-%m')=date_format(curdate(),'%Y-%m')")
    Integer getCurrMonthRegSubMember(String account);

    /**
     * 获取当月代理下线新增人数
     */
    @Query(nativeQuery = true, value = "select * from tb_player t where t.type=1 and t.leader=?1 and date_format(t.reg_time,'%Y-%m')=date_format(curdate(),'%Y-%m')")
    List<String> getCurrMonthRegSubMemberList(String account);

    /**
     * 获取账单类型
     */
    @Query(nativeQuery = true, value = "select " +
            "    v.v_name as title, " +
            "    v.v_value as type " +
            "from " +
            "    sys_code_table t " +
            "inner join sys_code_table_values v on t.id = v.fk_code_table_id " +
            "where " +
            "    t.t_code = 'commission_bill_type' and v.v_name is not null and v.v_name <> ''" +
            "order by v.v_order ")
    List<Map<String, Object>> getCommissionBillType();

    @Query(nativeQuery = true, value = "select " +
            "    v.v_name as title, " +
            "    v.v_value as type " +
            "from " +
            "    sys_code_table t " +
            "inner join sys_code_table_values v on t.id = v.fk_code_table_id " +
            "where " +
            "    t.t_code = 'other_bill_type' and v.v_name is not null and v.v_name <> '' " +
            "order by v.v_order ")
    List<Map<String, Object>> getOtherBillType();
}
