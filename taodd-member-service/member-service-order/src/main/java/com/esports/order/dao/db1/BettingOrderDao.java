package com.esports.order.dao.db1;


import com.esports.order.bean.db1.BettingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface BettingOrderDao extends JpaRepository<BettingOrder, Long>, JpaSpecificationExecutor<BettingOrder> {


    /**
     * 一级好友投注
     *
     * @param account
     * @return
     */
    @Query(value = ""
            + "select "
            + "		coalesce(sum(o.bet_amount),0) as amount"
            + " from "
            + "		tb_betting_order o"
            + " inner join("
            + "		select t.account from tb_player t where t.type=1 and t.inviter=?1"
            + " )u on o.account = u.account", nativeQuery = true)
	BigDecimal getFirstFriendsBetTotalAmount(String account);

    /**
     * 二级好友投注
     *
     * @param account
     * @return
     */
    @Query(value = ""
            + "select "
            + "		coalesce(sum(o.bet_amount),0) as amount"
            + " from "
            + "		tb_betting_order o"
            + " inner join("
            + "		select t2.account from tb_player t2 where t2.inviter in (select t1.account from tb_player t1 where t1.inviter=?1)"
            + " )u on o.account = u.account", nativeQuery = true)
    BigDecimal getSecondFriendsBetTotalAmount(String account);


    /**
     * 三级好友投注
     *
     * @param account
     * @return
     */
    @Query(value = ""
            + "select "
            + "		coalesce(sum(o.bet_amount),0) as amount"
            + " from "
            + "		tb_betting_order o"
            + " inner join("
            + "		 select t3.account from tb_player t3 where t3.inviter in ("
            + "			select t2.account from tb_player t2 where t2.inviter in(select t1.account from tb_player t1 where t1.inviter=?1)"
            + "		)"
            + " )u on o.account = u.account", nativeQuery = true)
    BigDecimal getThirdFriendsBetTotalAmount(String account);

    /**
     * 获取会员总的投注金额
     */
    @Query(value = ""
            + "select "
            + "      coalesce(sum(o.bet_amount),0) as amount"
            + " from "
            + "      tb_betting_order o"
            + " where o.account=?1 ", nativeQuery = true)
    BigDecimal getBetTotalAmount(String account);

    /**
     * 获取会员总输赢
     */
    @Query(value = ""
            + "select "
            + "      coalesce(sum(o.profit_amount),0) as amount"
            + " from "
            + "      tb_betting_order o"
            + " where o.account=?1 ", nativeQuery = true)
    BigDecimal getBetProfitTotalAmount(String account);

    /**
     * 获取会员投注总况
     */
    @Query(nativeQuery=true,
            value=" select "
                    + " count(t.account) betNum,"
                    + "	coalesce(sum(t.bet_total),0) betTotalAmount, "
                    + "	coalesce(sum(t.bet_amount),0) betAmount, "
                    + " coalesce(sum(t.profit_amount),0) profitTotalAmount"
                    + " from "
                    + "		tb_betting_order t"
                    + " where"
                    + "		t.account=?1 "
                    + "		and if(coalesce(?2,'') != '', t.platform=?2, 1=1)"
                    + "		and if(coalesce(?3,'') != '', t.game_type=?3,1=1)"
                    + "		and if(coalesce(?4,'') != '', t.game_name=?4,1=1)"
                    + "		and if(coalesce(?5,'') != '', t.create_time>=?5,1=1)"
                    + "		and if(coalesce(?6,'') != '', t.create_time<=?6,1=1)")
    Map<String,Object> getBetSummary(String account, String apiCode, String gameType, String gameName, String startTime, String endTime);

    @Query(nativeQuery=true,
            value=" select "
                    + " count(t.account) betNum,"
                    + "	coalesce(sum(t.bet_total),0) betTotal, "
                    + "	coalesce(sum(t.bet_amount),0) betAmount, "
                    + " coalesce(sum(t.profit_amount),0) profitAmount,"
                    + " max(t.approval_time) approvalTime,"
                    + " t.platform platform,"
                    + " t.game_type gameType "
                    + " from "
                    + "		tb_betting_order t"
                    + " where"
                    + "		t.account=?1 "
                    + "		and if(coalesce(?2,'') != '', t.platform=?2, 1=1)"
                    + "		and if(coalesce(?3,'') != '', t.game_type=?3,1=1)"
                    + "		and if(coalesce(?4,'') != '', t.create_time>=?4,1=1)"
                    + "		and if(coalesce(?5,'') != '', t.create_time<=?5,1=1)"
                    + " group by t.account, t.platform, t.game_type")
    List<Map<String,Object>> getBetGameSummary(String account, String startTime, String endTime);
}
