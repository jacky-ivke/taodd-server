package com.esports.center.release.dao.db1;

import com.esports.center.release.bean.db1.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ActivityDao extends JpaRepository<Activity, Long>, JpaSpecificationExecutor<Activity> {


    @Query(nativeQuery = true, value = "select " +
            "    v.v_name as name, " +
            "    v.v_value as type " +
            "from " +
            "    sys_code_table t " +
            "inner join sys_code_table_values v on t.id = v.fk_code_table_id " +
            "where " +
            "    t.t_code = 'activity_type' and v.v_value <> ''" +
            "order by v.v_order ")
    List<Map<String, Object>> getActivityTypes();

    @Query(nativeQuery = true, value = "select " +
            "    v.v_name as name, " +
            "    v.v_value as type " +
            "from " +
            "    sys_code_table t " +
            "inner join sys_code_table_values v on t.id = v.fk_code_table_id " +
            "where " +
            "    t.t_code = 'activity_attr' and v.v_value <> ''" +
            "order by v.v_order ")
    List<Map<String, Object>> getActivityAttrs();

}
