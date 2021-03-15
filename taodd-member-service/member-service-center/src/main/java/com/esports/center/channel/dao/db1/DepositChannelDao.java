package com.esports.center.channel.dao.db1;

import com.esports.center.channel.bean.db1.DepositChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DepositChannelDao extends JpaRepository<DepositChannel, Long> {

    @Query(nativeQuery = true,
            value = "select " +
                    "	t.id " +
                    "from " +
                    "	tb_grade_scheme t " +
                    "inner join tb_player m on t.scheme_code = m.grade_code " +
                    "where " +
                    "	m.account=?1 and m.type = 1")
    Long getGradeIdByAccount(String account);

    @Query(nativeQuery = true,
            value = "select " +
                    "	* " +
                    "from " +
                    "	tb_deposit_channel l " +
                    "inner join  " +
                    "	tb_grade_deposit r " +
                    "on l.id = r.deposit_id " +
                    "where r.grade_id =?1 and l.ok_status=1")
    List<DepositChannel> getChannels(Long gradeId);

    @Query(nativeQuery = true, value = "select " +
            "    v.v_name as title, " +
            "    v.v_value as type " +
            "from " +
            "    sys_code_table t " +
            "inner join sys_code_table_values v on t.id = v.fk_code_table_id " +
            "where " +
            "    t.t_code in('company_deposit_type','online_deposit_type') " +
            "order by v.v_order ")
    List<Map<String, Object>> getChannelTypes();

    @Query(nativeQuery = true, value = "select " +
            "    v.v_name as name, " +
            "    v.v_value as type " +
            "from " +
            "    sys_code_table t " +
            "inner join sys_code_table_values v on t.id = v.fk_code_table_id " +
            "where " +
            "    t.t_code ='deposit_order_type' " +
            "order by v.v_order ")
    List<Map<String, Object>> getDepositOrderTypes();


}
