package com.esports.order.dao.db1;

import com.esports.order.bean.db1.DrawOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;

@Repository
public interface DrawOrderDao extends JpaRepository<DrawOrder, Long>, JpaSpecificationExecutor<DrawOrder> {

    /**
     * 获取会员取款总额
     */
    @Query(nativeQuery = true, value = " select ifnull(sum(t.amount),0) drawAmount from ( "
            + "	select t.ok_status, t.account, t.amount,t.create_time from tb_draw_order t "
            + "	union all  "
            + "	select t.ok_status, t.account, t.amount,t.create_time from tb_manual_order t where t.type = 'draw' "
            + " )t "
            + " where t.account = ?1 "
            + " and if(ifnull(?2,'') != '', t.ok_status=?2, 1=1)"
            + " and if(ifnull(?3,'') != '', t.create_time>=?3, 1=1)"
            + " and if(ifnull(?4,'') != '', t.create_time<=?4, 1=1)"
            + "")
    BigDecimal getDrawSummary(String account, Integer okStatus, String startTime, String endTime);

    @Query(nativeQuery = true, value = " select t.* "
            + " from ("
            + "	select order_no orderNo, t.ok_status okStatus, t.account, t.amount,t.create_time createTime, t.bank_account bankAccount from tb_draw_order t "
            + "	union all  "
            + "	select order_no orderNo, t.ok_status okStatus, t.account, t.amount,t.create_time createTime, '人工取款' bankAccount  from tb_manual_order t where t.type = 'draw') t "
            + " where 1=1 "
            + "   and if(ifnull(?1,'') != '', t.account=?1, 1=1)"
            + "   and if(ifnull(?2,'') != '', t.okStatus=?2, 1=1)"
            + "   and if(ifnull(?3,'') != '', t.createTime>=?3, 1=1)"
            + "   and if(ifnull(?4,'') != '', t.createTime<=?4, 1=1)"
            + " order by "
            + "    t.createTime desc",
            countQuery = "select count(account) from ( "
                    + " select t.account "
                    + " from ("
                    + "	select order_no, t.ok_status, t.account, t.amount,t.create_time, t.bank_account from tb_draw_order t "
                    + "	union all  "
                    + "	select order_no, t.ok_status, t.account, t.amount,t.create_time, '人工取款' bank_account from tb_manual_order t where t.type = 'draw') t "
                    + " where 1=1 "
                    + "   and if(ifnull(?1,'') != '', t.account=?1, 1=1)"
                    + "   and if(ifnull(?2,'') != '', t.ok_status=?2, 1=1)"
                    + "   and if(ifnull(?3,'') != '', t.create_time>=?3, 1=1)"
                    + "   and if(ifnull(?4,'') != '', t.create_time<=?4, 1=1)"
                    + ")t")
    Page<Map<String, Object>> findAll(String account, Integer okStatus, String startTime, String endTime, Pageable pageable);

    @Query(nativeQuery = true, value = " select count(t.account) as drawCount, coalesce(sum((case when t.ok_status = 1 then t.amount else 0 end)),0) drawAmount from ( "
            + "	select t.ok_status, t.account, t.amount,t.approval_time create_time from tb_draw_order t"
            + "	union all  "
            + "	select t.ok_status, t.account, t.amount,t.create_time from tb_manual_order t where t.type = 'draw' "
            + ")t where t.account = ?1 and date(t.create_time)=curdate()")
    Map<String, Object> getTodayDrawTotal(String account);
}
