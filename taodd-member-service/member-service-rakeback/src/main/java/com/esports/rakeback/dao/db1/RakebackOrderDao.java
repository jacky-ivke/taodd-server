package com.esports.rakeback.dao.db1;

import com.esports.rakeback.bean.db1.RakebackOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Repository
public interface RakebackOrderDao extends JpaRepository<RakebackOrder, Long>, JpaSpecificationExecutor<RakebackOrder> {

    /**
     * 本期各个游戏平台可结算的投注（由于当前系统共用数据库，对tb_betting_order依赖暂时不处理）
     *
     * @param account
     * @param platform
     * @param gameType
     * @return
     */
    @Query(nativeQuery = true, value =
            "select " +
                    "   coalesce(sum(t.bet_amount),0) " +
                    " from " +
                    "   tb_betting_order t " +
                    " left join(" +
                    "   select o.account, o.platform, o.game_type, max(o.approval_time)approval_time,max(o.create_time)create_time from tb_rakeback_detail o  group by o.account, o.platform, o.game_type" +
                    " )t1 on t.account = t1.account and t.platform=t1.platform and t.game_type = t1.game_type" +
                    " where t.account=?1 and t.platform=?2 and t.game_type=?3 and if(t1.approval_time is not null, t.approval_time>t1.approval_time, 1=1)")
    BigDecimal getCurrentSettlementWashing(String account, String platform, String gameType);

    /**
     * 获取指定时间范围内已返水金额
     *
     * @param account
     * @param startTime
     * @param endTime
     * @return
     */
    @Query(nativeQuery = true, value =
            "select " +
                    "   coalesce(sum(t.bet_amount),0) " +
                    "from " +
                    "   tb_rakeback_detail t " +
                    " where t.account=?1 and t.create_time>=?2 and t.create_time<=?3 and t.platform=?4 and t.game_type=?5")
    BigDecimal getSectionRakeAmount(String account, Timestamp startTime, Timestamp endTime, String platform, String gameType);
}
