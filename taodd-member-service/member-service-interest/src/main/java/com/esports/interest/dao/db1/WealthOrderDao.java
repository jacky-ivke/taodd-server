package com.esports.interest.dao.db1;

import com.esports.interest.bean.db1.WealthOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface WealthOrderDao extends JpaRepository<WealthOrder, Long>, JpaSpecificationExecutor<WealthOrder> {

    /**
     * 获取投资理财报表
     *
     * @param account
     * @return
     */
    @Query(nativeQuery = true, value =
            "select " +
                    "    coalesce(sum(case when t.approval_time<=now() " +
                    "        and if(t.receive_time is not null, t.receive_time>=t.approval_time,1=1) then t.profit else 0 end),0) interestTotalAmount, " +
                    "    coalesce(sum(case when date_format(t.approval_time,'%Y-%m')=date_format(curdate(),'%Y-%m') and t.approval_time<=now() " +
                    "        and if(t.receive_time is not null, t.receive_time>=t.approval_time,1=1) then t.profit else 0 end),0) interestMonthAmount, " +
                    "    coalesce(sum(case when yearweek(date_format(t.approval_time,'%Y-%m-%d'))=yearweek(now()) and t.approval_time<=now() " +
                    "        and if(t.receive_time is not null, t.receive_time>=t.approval_time,1=1) then t.profit else 0 end),0) interestWeekAmount, " +
                    "    coalesce(sum(case when year(t.approval_time)=year(now()) and t.approval_time<=now() " +
                    "        and if(t.receive_time is not null, t.receive_time>=t.approval_time,1=1) then t.profit else 0 end),0) interestYearAmount, " +
                    "    coalesce(sum(case when date(t.approval_time) = date(now()) and t.approval_time<=now() " +
                    "        and if(t.receive_time is not null, t.receive_time>=t.approval_time,1=1) then t.profit else 0 end),0) interestTodayAmount, " +
                    "    coalesce(sum(case when date(t.approval_time) = date_sub(curdate(),interval -1 day) and t.approval_time<=now() " +
                    "        and if(t.receive_time is not null, t.receive_time>=t.approval_time,1=1) then t.profit else 0 end),0) interestYesterDayAmount " +
                    "from " +
                    "    tb_wealth_order t where t.account=?1")
    Map<String, Object> getInterestReport(String account);

    /**
     * 获取投资理财订单
     *
     * @param orderNo
     * @return
     */
    WealthOrder findByOrderNo(String orderNo);

    /**
     * 获取利息资产总额
     *
     * @param account
     * @return
     */
    @Query(nativeQuery = true, value = "select coalesce(sum(case when t.receive = 0 and t.approval_time <= now() then truncate((t.amount+t.profit),2) when t.receive = 0 and t.approval_time > now() then t.amount else 0 end),0)  "
            + "from tb_wealth_order t where t.account=?1")
    BigDecimal getTotalInterest(String account);

    @Query(nativeQuery = true, value = " select "
            + "	coalesce(sum(case when t.receive=0 then t.amount else 0 end),0) frozenAmount, "
            + " coalesce(sum(case when t.receive=0 and t.approval_time <= now() then t.amount else 0 end),0) availableAmount"
            + " from "
            + "		tb_wealth_order t"
            + " where "
            + "		t.account=?1 "
            + "		and if(?2 != '', t.create_time>=?2,1=1)"
            + "		and if(?3 != '', t.create_time<=?3,1=1)")
    Map<String, Object> getValidSummary(String account, String startTime, String endTime);

    /**
     * 获取会员利息钱包到期可领取订单
     * @param account
     * @return
     */
    @Query(nativeQuery = true, value = "select * from tb_wealth_order where account = ?1 and receive=0 and approval_time<=now()")
    List<WealthOrder>getDueOrders(String account);

}
