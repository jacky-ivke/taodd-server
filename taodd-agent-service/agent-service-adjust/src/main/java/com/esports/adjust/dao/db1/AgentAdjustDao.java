package com.esports.adjust.dao.db1;

import com.esports.adjust.bean.db1.AdjustOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface AgentAdjustDao extends JpaRepository<AdjustOrder, Long>, JpaSpecificationExecutor<AdjustOrder> {

    @Query(nativeQuery = true, value = "select " +
            "    count(t.agent) adjustTotal, " +
            "    count(distinct case when t.ok_status=1 then t.account else null end) adjustMember," +
            "    count(case when date_format(t.approval_time,'%Y-%m') = date_format(curdate(),'%Y-%m') then t.agent else null end) currentMonthAdjust " +
            " from " +
            "    tb_agent_ajust t where t.agent=?1")
    Map<String, Object> getAdjustInfo(String account);
}
