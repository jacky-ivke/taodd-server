package com.esports.order.dao.db1;

import com.esports.order.bean.db1.DepositOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;

@Repository
public interface DepositOrderDao extends JpaRepository<DepositOrder, Long>, JpaSpecificationExecutor<DepositOrder> {


    @Query(nativeQuery = true, value = " select t.* "
            + " from ("
            + "	select order_no orderNo, t.ok_status okStatus, t.account, (case when t.ok_status = 1 then t.actual_amount else t.amount end) amount,t.create_time createTime, t.channel_name channalName from tb_deposit_order t where t.identity='member' "
            + "	union all  "
            + "	select order_no orderNo, t.ok_status okStatus, t.account,t.amount,t.create_time createTime, '人工存款' channalName  from tb_manual_order t where t.type = 'deposit' "
            + "	union all  "
            + "	select order_no orderNo, t.ok_status okStatus, t.member account, t.amount,t.create_time createTime, '代理代存' channalName  from tb_transfer_submember t "
            + "	union all  "
            + "	select order_no orderNo, t.ok_status okStatus, t.account,t.amount,t.create_time createTime, t.channel_name channalName from tb_deposit_order t where t.identity='agent' and t.bank_account='center') t "
            + " where 1=1 "
            + "   and if(ifnull(?1,'') != '', t.account=?1, 1=1)"
            + "   and if(ifnull(?2,'') != '', t.okStatus=?2, 1=1)"
            + "   and if(ifnull(?3,'') != '', t.createTime>=?3, 1=1)"
            + "   and if(ifnull(?4,'') != '', t.createTime<=?4, 1=1)"
            + " order by "
            + "    t.createTime desc",
            countQuery = "select count(order_no) from ( "
                    + " select t.order_no "
                    + " from("
                    + "	select order_no, t.ok_status, t.account,t.amount,t.create_time, t.channel_name from tb_deposit_order t where t.identity='member'"
                    + "	union all  "
                    + "	select order_no, t.ok_status, t.account,t.amount,t.create_time, '人工存款' channel_name from tb_manual_order t where t.type = 'deposit' "
                    + "	union all  "
                    + "	select order_no, t.ok_status, t.member account, t.amount,t.create_time, '代理代存' channel_name from tb_transfer_submember t "
                    + "	union all  "
                    + "	select order_no, t.ok_status, t.account,t.amount,t.create_time, t.channel_name from tb_deposit_order t where t.identity='agent' and t.bank_account='center')t "
                    + " where 1=1 "
                    + "   and if(ifnull(?1,'') != '', t.account=?1, 1=1)"
                    + "   and if(ifnull(?2,'') != '', t.ok_status=?2, 1=1)"
                    + "   and if(ifnull(?3,'') != '', t.create_time>=?3, 1=1)"
                    + "   and if(ifnull(?4,'') != '', t.create_time<=?4, 1=1)"
                    + ")t")
    Page<Map<String,Object>> findAll(String account, Integer okStatus, String startTime, String endTime, Pageable pageable);


    @Query(nativeQuery = true,
            value = " select coalesce(sum(t.amount),0) depositAmount from ( "
                    + "	select t.ok_status, t.account, (case when t.ok_status = 1 then t.actual_amount else t.amount end) amount,t.create_time from tb_deposit_order t where t.identity='member'"
                    + "	union all  "
                    + "	select t.ok_status, t.account, t.amount,t.create_time from tb_manual_order t where t.type = 'deposit' "
                    + "	union all  "
                    + "	select t.ok_status, t.member account, t.amount,t.create_time from tb_transfer_submember t "
                    + "	union all  "
                    + "	select t.ok_status, t.account, t.amount,t.create_time from tb_draw_order t where t.identity = 'agent' and t.bank_account='center' "
                    + ")t "
                    + "where t.account = ?1 "
                    + "and if(ifnull(?2, '') != '', t.ok_status=?2, 1=1)"
                    + "and if(ifnull(?3, '') != '', t.create_time>=?3, 1=1)"
                    + "and if(ifnull(?4,'') != '', t.create_time<=?4, 1=1)"
                    + "")
    BigDecimal getDepositSummary(String account, Integer okStatus, String startTime, String endTime);

    @Query(nativeQuery = true,
            value = "select count(id) depositTotalCount, "
                    +" count(case when create_time>=date_sub(now(),interval 10 minute) then account else null end) depositNum, "
                    +" count(case when create_time>=date_sub(now(),interval 10 minute) and ok_status=1 then account else null end) depositSuccessNum, "
                    +" count(case when create_time>=date_sub(now(),interval 10 minute) and ok_status<>1 then account else null end) depositFailureNum "
                    +" from tb_deposit_order where account=?1 and identity='member' and date(create_time)=date(now())")
    Map<String,Object> getTodayOnlineDepositTotal(String account);
}
