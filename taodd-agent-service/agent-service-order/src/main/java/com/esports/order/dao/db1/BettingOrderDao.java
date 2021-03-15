package com.esports.order.dao.db1;

import com.esports.order.bean.db1.BettingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface BettingOrderDao extends JpaRepository<BettingOrder, Long>, JpaSpecificationExecutor<BettingOrder> {


    /**
     * 获取玩家总输赢
     */
    @Query(nativeQuery = true, value = ""
            + "select "
            + "      coalesce(sum(o.profit_amount),0) as amount"
            + " from "
            + "      tb_betting_order o"
            + " where o.account=?1 ")
    BigDecimal getMemberProfitAmount(String account);
}
