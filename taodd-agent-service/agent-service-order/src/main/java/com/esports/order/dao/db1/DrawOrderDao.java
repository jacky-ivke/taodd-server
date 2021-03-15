package com.esports.order.dao.db1;

import com.esports.order.bean.db1.DrawOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface DrawOrderDao extends JpaRepository<DrawOrder, Long>, JpaSpecificationExecutor<DrawOrder> {


    /**
     * 获取代理待审提取金额
     *
     * @param account
     * @return
     */
    @Query(nativeQuery = true, value = "select "
            + "  coalesce(sum(t.amount),0) "
            + " from "
            + "  tb_draw_order t where t.account=?1 and t.identity='agent' and t.ok_status=0 ")
    BigDecimal getAgentPendingDrawAmount(String account);

    @Query(nativeQuery = true, value = "select coalesce(sum(t.amount),0) from ( "
            + "   select t.account,t.amount amount from tb_draw_order t where t.ok_status=1"
            + "   union all "
            + "   select t.account,t.amount from tb_manual_order t where t.type = 'draw'"
            + ")t where t.account=?1")
    BigDecimal getMemberDrawAmount(String account);

}
