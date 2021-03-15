package com.esports.order.dao.db1;

import com.esports.order.bean.db1.DepositOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface DepositOrderDao extends JpaRepository<DepositOrder, Long>, JpaSpecificationExecutor<DepositOrder> {

    /**
     * 获取会员总存款金额
     */
    @Query(nativeQuery = true, value = "select coalesce(sum(t.amount),0) from ( " +
            "   select t.account,t.actual_amount amount from tb_deposit_order t where t.ok_status = 1 " +
            "   union all " +
            "   select t.account,t.amount from tb_manual_order t where t.type = 'deposit' and t.ok_status = 1 " +
            ")t where t.account=?1")
    BigDecimal getMemberDepositAmount(String account);
}
