package com.esports.patner.dao.db1;

import com.esports.patner.bean.db1.PartnerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface PartnerOrderDao extends JpaRepository<PartnerOrder, Long>, JpaSpecificationExecutor<PartnerOrder> {


    @Query(nativeQuery = true,
            value = " select "
                    + " coalesce(sum(t.bet_amount),0) betTotalAmount, "
                    + " coalesce(truncate(sum(case when type='first' then t.bet_amount else 0 end),2),0) firstBetTotalAmount,"
                    + " coalesce(truncate(sum(case when type='second' then t.bet_amount else 0 end),2),0) secondBetTotalAmount,"
                    + " coalesce(truncate(sum(case when type='third' then t.bet_amount else 0 end),2),0) thirdBetTotalAmount,"
                    + " coalesce(truncate(sum(case when type='first' then t.bet_amount * (t.percentage / 100) else 0 end),2),0)+"
                    + " coalesce(truncate(sum(case when type='second' then t.bet_amount * (t.percentage / 100) else 0 end),2),0)+"
                    + " coalesce(truncate(sum(case when type='third' then t.bet_amount * (t.percentage / 100) else 0 end),2),0) inviteCommissionAmount"
                    + "	from "
                    + "		tb_partner_order t "
                    + " where t.ok_status = 1 and t.account=?1 and if(ifnull(?2,'') != '', t.create_time>=?2, 1=1) and if(ifnull(?3,'') != '', t.create_time<=?3, 1=1)")
    Map<String, Object> getPartnerFriendsBetSummary(String account, String startTime, String endTime);
}
	