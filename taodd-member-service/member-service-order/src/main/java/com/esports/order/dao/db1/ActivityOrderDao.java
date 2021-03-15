package com.esports.order.dao.db1;

import com.esports.order.bean.db1.ActivityOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Repository
public interface ActivityOrderDao extends JpaRepository<ActivityOrder, Long>, JpaSpecificationExecutor<ActivityOrder> {

    @Query(nativeQuery = true, value = "select "
            + "	coalesce(sum(o.amount),0) "
            + "from "
            + "	tb_activity_order o where o.ok_status  = 1 and date_format(o.approval_time,'%y-%m') = date_format(now(),'%y-%m')")
    BigDecimal getCurrMonthDiscountTotal();

    @Query(nativeQuery = true, value = " select "
            + "	coalesce(sum(t.amount),0) activityAmount "
            + " from "
            + "		tb_activity_order t"
            + " where "
            + "		t.account=?1 "
            + "		and if(ifnull(?2,'') != '', t.ok_status=?2, 1=1)"
            + "		and if(ifnull(?3,'') != '', t.type=?3,1=1)"
            + "		and if(ifnull(?4,'') != '', t.create_time>=?4,1=1)"
            + "		and if(ifnull(?5,'') != '', t.create_time<=?5,1=1)")
    BigDecimal getActiveSummary(String account, Integer okStatus, String type, String startTime, String endTime);

    @Query(nativeQuery = true, value = "select "
            + "     max(approval_time) "
            + "	from "
            + "     tb_activity_order "
            + "	where "
            + "     ok_status=1 and account=?1 and type=?2")
    String getLastAwardTime(String account, String atyType);
}
