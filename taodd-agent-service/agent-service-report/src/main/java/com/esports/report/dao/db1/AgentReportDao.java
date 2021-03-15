package com.esports.report.dao.db1;

import com.esports.report.bean.db1.AgentReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
public interface AgentReportDao extends JpaRepository<AgentReport, Long>, JpaSpecificationExecutor<AgentReport> {

    /**
     * 获取代理当月注册人数
     */
    @Query(nativeQuery = true, value = "select count(t.id) from tb_player t where t.type=1 and t.leader=?1 and date_format(t.reg_time,'%Y-%m')=date_format(curdate(),'%Y-%m')")
    Integer getCurrMonthRegSubMember(String account);

    /**
     * 获取代理下线用户当月注册且充值的用户
     */
    @Query(nativeQuery = true, value = "select count(distinct t.account) from tb_player t inner join ( " +
            "   select t.account,t.actual_amount amount, t.approval_time create_time from tb_deposit_order t where t.ok_status = 1 and date_format(t.approval_time,'%Y-%m')=date_format(curdate(),'%Y-%m')" +
            "   union all " +
            "   select t.account,t.amount,t.create_time from tb_manual_order t where t.type = 'deposit' and t.ok_status = 1 and date_format(t.create_time,'%Y-%m')=date_format(curdate(),'%Y-%m')" +
            ")t1 on t.account = t1.account where t.leader=?1 and date_format(t.reg_time,'%Y-%m')=date_format(curdate(),'%Y-%m')")
    Integer getCurrMonthSubMemberFirstDeposit(String account);

    /**
     * 获取代理下线用户当月活跃的玩家
     */
    @Query(nativeQuery = true, value = "select count(distinct t.account) from tb_player t inner join ( " +
            "   select t.account from tb_deposit_order t where t.ok_status = 1 and date_format(t.approval_time,'%Y-%m')=date_format(curdate(),'%Y-%m')" +
            "   union all " +
            "   select t.account from tb_manual_order t where t.type = 'deposit' and t.ok_status = 1 and date_format(t.create_time,'%Y-%m')=date_format(curdate(),'%Y-%m')" +
            "   union all " +
            "   select t.account from tb_betting_order t where date_format(t.create_time,'%Y-%m')=date_format(curdate(),'%Y-%m')" +
            ")t1 on t.account = t1.account where  t.leader=?1 and t.type =1")
    Integer getCurrMonthActvieSubMember(String account);

    /**
     * 获取当月代理下线用户投注总盈亏
     */
    @Query(nativeQuery = true, value = "select coalesce(sum(o.profit_amount),0)amount from tb_betting_order o inner join tb_player t on o.account=t.account where date_format(o.create_time,'%Y-%m')=date_format(now(),'%Y-%m') and t.leader=?1 ")
    BigDecimal getCurrMonthSubMemberBetWinAmount(String account);

    /**
     * 获取当月代理下线用户优惠奖励总额
     */
    @Query(nativeQuery = true, value = "select  coalesce(sum(o.amount),0) from tb_activity_order o inner join tb_player t on o.account = t.account where date_format(o.approval_time,'%Y-%m')=date_format(now(),'%Y-%m') and  t.leader=?1 and t.type = 1")
    BigDecimal getCurrMonthSubMemberAwardAmount(String account);

    /**
     * 获取代理下线用户在指定时间段内的取款金额
     */
    @Query(nativeQuery = true, value = "select coalesce(sum(t1.amount),0) from tb_player t inner join ( "
            + "   select t.account,t.amount, t.approval_time create_time from tb_draw_order t where t.ok_status = 1 "
            + "   union all "
            + "   select t.account,t.amount, t.create_time from tb_manual_order t where t.type = 'draw' and t.ok_status = 1 "
            + ")t1 on t.account = t1.account where t.leader=?1 and t1.create_time>=?2 and t1.create_time<=?3")
    BigDecimal getSubMemberDrawAmount(String account, Timestamp startTime, Timestamp endTime);

    /**
     * 获取代理下线用户在指定时间段内的存款金额
     */
    @Query(nativeQuery = true, value = "select coalesce(sum(t1.amount),0) from tb_player t inner join ( " +
            "   select t.account,t.actual_amount amount, t.approval_time create_time from tb_deposit_order t where t.ok_status = 1 " +
            "   union all " +
            "   select t.account,t.amount, t.create_time from tb_manual_order t where t.type = 'deposit' and t.ok_status = 1 " +
            ")t1 on t.account = t1.account where  t.leader=?1 and t.type = 1 and t1.create_time>=?2 and t1.create_time<=?3")
    BigDecimal getSubMemberDepositAmount(String account, Timestamp startTime, Timestamp endTime);

    /**
     * 获取代理下线用户在指定时间段内的优惠奖励总额
     */
    @Query(nativeQuery = true, value = "select coalesce(sum(o.amount),0) from tb_activity_order o inner join tb_player t on o.account = t.account where t.leader=?1 and t.type = 1 and o.approval_time>=?2 and o.approval_time<=?3")
    BigDecimal getSubMemberAwardAmount(String account, Timestamp startTime, Timestamp endTime);

    /**
     * 获取代理下线用户在指定时间段内投注总输赢
     */
    @Query(nativeQuery = true, value = "select coalesce(sum(o.profit_amount),0) as amount from tb_betting_order o inner join tb_player t on o.account=t.account where t.leader=?1 and o.create_time>=?2 and o.create_time<=?3")
    BigDecimal getSubMemberBetWin(String account, Timestamp startTime, Timestamp endTime);

    /**
     * 获取代理下线用户在指定时间段内平台费
     */
    @Query(nativeQuery = true, value = "select coalesce(sum(platform_fee),0) from tb_commission_order where ok_status <> '2' and account = ?1 and create_time >=?2 and create_time <=?3")
    BigDecimal getAgentPlatformFee(String account, Timestamp startTime, Timestamp endTime);

    /**
     * 获取代理下线用户在指定时间段内返水总额
     */
    @Query(nativeQuery = true, value = "select coalesce(sum(o.actual_amount),0) from tb_rakeback_order o inner join tb_player t on o.account = t.account where  t.leader=?1 and t.type = 1 and o.create_time>=?2 and o.create_time<=?3")
    BigDecimal getSubMemberRakeAmount(String account, Timestamp startTime, Timestamp endTime);

    /**
     * 获取代理返佣信息
     */
    @Query(nativeQuery = true, value = "select"
            + " profit_amount as profitAmount, commission_amount as commissionAmount,platform_fee as platformFee, "
            + " api_take_amount as apiTakeAmount,rakeback_amount as rakeAmount,discount_amount as discountAmount,"
            + " deposit_amount as depositAmount, draw_amount as drawAmount,actual_amount as actualAmount, unsettled_amount as unsettledAmount, create_time as lastCreateTime"
            + " from tb_commission_order where account = ?1 and month=?2 order by create_time")
    List<Map<String, Object>> getAgentCommission(String account, String month);

    @Query(nativeQuery = true, value = "select count(distinct account) from tb_commission_detail where agent = ?1 and `month`= ?2")
    Integer getCommissionActiveMember(String account, String month);

    /**
     * 获取代理各个平台的平台费用
     */
    @Query(nativeQuery = true, value = "select "
            + " t.app_code apiCode,"
            + " t1.game_type gameType,"
            + " coalesce(t1.default_fee,0) percentage,"
            + " truncate(abs(coalesce(if(t2.profit_amount>0,0,t2.profit_amount),0)) * (coalesce(t1.default_fee,0) / 100),2) platformFee,"
            + " coalesce(t2.profit_amount,0) profitAmount, "
            + " coalesce(t2.bet_amount,0) betAmount "
            + " from tb_external_app t"
            + " left join tb_app_type t1 on t.app_code = t1.api_code and t1.is_used=1 "
            + " left join ("
            + " 		select "
            + "				o.platform, "
            + "				o.game_type,"
            + "				coalesce(sum(o.bet_amount),0) bet_amount,"
            + "				coalesce(sum(o.profit_amount),0) as profit_amount"
            + "			from tb_betting_order o inner join tb_player t on o.account=t.account"
            + "			left join (select o.account, o.platform, o.game_type, max(o.approval_time) last_approval_time from tb_commission_detail o group by o.account, o.platform, o.game_type) c"
            + " 		on t.account=c.account and o.platform=c.platform and o.game_type=c.game_type"
            + "			where t.leader=?1 and if(c.last_approval_time is not null, o.approval_time>c.last_approval_time,1=1) "
            + "			group by o.platform, o.game_type"
            + " )t2 on t.app_code = t2.platform and t1.game_type = t2.game_type")
    List<Map<String, Object>> getAgentPlatformProfit(String account);

    /**
     * 获取代理当前活动分摊
     */
    @Query(nativeQuery = true, value = "select ifnull(sum(o.amount),0) from tb_player m left join("
            + "     select o.account, max(o.create_time) last_commission_time from tb_commission_order o group by o.account"
            + " )t2 on m.leader=t2.account"
            + " left join tb_activity_order o on m.account = o.account"
            + " where o.ok_status=1 and if(t2.last_commission_time is not null, o.approval_time>t2.last_commission_time,1=1) and m.leader=?1")
    BigDecimal getCommissionSubMemberAtyAmount(String account);


    /**
     * 获取指定时间，代理下线新注册人数
     *
     * @param account
     * @param month
     * @return
     */
    @Query(nativeQuery = true, value = "select "
            + "         ifnull(count(distinct case when day(t.reg_time) = 1 then t.account else null end),0) as '1d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 2 then t.account else null end),0) as '2d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 3 then t.account else null end),0) as '3d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 4 then t.account else null end),0) as '4d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 5 then t.account else null end),0) as '5d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 6 then t.account else null end),0) as '6d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 7 then t.account else null end),0) as '7d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 8 then t.account else null end),0) as '8d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 9 then t.account else null end),0) as '9d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 10 then t.account else null end),0) as '10d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 11 then t.account else null end),0) as '11d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 12 then t.account else null end),0) as '12d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 13 then t.account else null end),0) as '13d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 14 then t.account else null end),0) as '14d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 15 then t.account else null end),0) as '15d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 16 then t.account else null end),0) as '16d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 17 then t.account else null end),0) as '17d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 18 then t.account else null end),0) as '18d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 19 then t.account else null end),0) as '19d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 20 then t.account else null end),0) as '20d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 21 then t.account else null end),0) as '21d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 22 then t.account else null end),0) as '22d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 23 then t.account else null end),0) as '23d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 24 then t.account else null end),0) as '24d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 25 then t.account else null end),0) as '25d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 26 then t.account else null end),0) as '26d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 27 then t.account else null end),0) as '27d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 28 then t.account else null end),0) as '28d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 29 then t.account else null end),0) as '29d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 30 then t.account else null end),0) as '30d',"
            + "         ifnull(count(distinct case when day(t.reg_time) = 31 then t.account else null end),0) as '31d'"
            + " from tb_player t where t.leader=?1 and t.type=1 and date_format(t.reg_time,'%Y-%m') = ?2")
    Map<String, Object> getSubMemberRegisterByDate(String account, String month);


    /**
     * 数据对比，新注册存款金额：时间段内注册用户的累计存款金额
     *
     * @param account
     * @param month
     * @return
     */
    @Query(nativeQuery = true, value = " select "
            + "         ifnull(sum(case when day(t.create_time) = 1 then t.amount else 0 end),0) as '1d',"
            + "         ifnull(sum(case when day(t.create_time) = 2 then t.amount else 0 end),0) as '2d',"
            + "         ifnull(sum(case when day(t.create_time) = 3 then t.amount else 0 end),0) as '3d',"
            + "         ifnull(sum(case when day(t.create_time) = 4 then t.amount else 0 end),0) as '4d',"
            + "         ifnull(sum(case when day(t.create_time) = 5 then t.amount else 0 end),0) as '5d',"
            + "         ifnull(sum(case when day(t.create_time) = 6 then t.amount else 0 end),0) as '6d',"
            + "         ifnull(sum(case when day(t.create_time) = 7 then t.amount else 0 end),0) as '7d',"
            + "         ifnull(sum(case when day(t.create_time) = 8 then t.amount else 0 end),0) as '8d',"
            + "         ifnull(sum(case when day(t.create_time) = 9 then t.amount else 0 end),0) as '9d',"
            + "         ifnull(sum(case when day(t.create_time) = 10 then t.amount else 0 end),0) as '10d',"
            + "         ifnull(sum(case when day(t.create_time) = 11 then t.amount else 0 end),0) as '11d',"
            + "         ifnull(sum(case when day(t.create_time) = 12 then t.amount else 0 end),0) as '12d',"
            + "         ifnull(sum(case when day(t.create_time) = 13 then t.amount else 0 end),0) as '13d',"
            + "         ifnull(sum(case when day(t.create_time) = 14 then t.amount else 0 end),0) as '14d',"
            + "         ifnull(sum(case when day(t.create_time) = 15 then t.amount else 0 end),0) as '15d',"
            + "         ifnull(sum(case when day(t.create_time) = 16 then t.amount else 0 end),0) as '16d',"
            + "         ifnull(sum(case when day(t.create_time) = 17 then t.amount else 0 end),0) as '17d',"
            + "         ifnull(sum(case when day(t.create_time) = 18 then t.amount else 0 end),0) as '18d',"
            + "         ifnull(sum(case when day(t.create_time) = 19 then t.amount else 0 end),0) as '19d',"
            + "         ifnull(sum(case when day(t.create_time) = 20 then t.amount else 0 end),0) as '20d',"
            + "         ifnull(sum(case when day(t.create_time) = 21 then t.amount else 0 end),0) as '21d',"
            + "         ifnull(sum(case when day(t.create_time) = 22 then t.amount else 0 end),0) as '22d',"
            + "         ifnull(sum(case when day(t.create_time) = 23 then t.amount else 0 end),0) as '23d',"
            + "         ifnull(sum(case when day(t.create_time) = 24 then t.amount else 0 end),0) as '24d',"
            + "         ifnull(sum(case when day(t.create_time) = 25 then t.amount else 0 end),0) as '25d',"
            + "         ifnull(sum(case when day(t.create_time) = 26 then t.amount else 0 end),0) as '26d',"
            + "         ifnull(sum(case when day(t.create_time) = 27 then t.amount else 0 end),0) as '27d',"
            + "         ifnull(sum(case when day(t.create_time) = 28 then t.amount else 0 end),0) as '28d',"
            + "         ifnull(sum(case when day(t.create_time) = 29 then t.amount else 0 end),0) as '29d',"
            + "         ifnull(sum(case when day(t.create_time) = 30 then t.amount else 0 end),0) as '30d',"
            + "         ifnull(sum(case when day(t.create_time) = 31 then t.amount else 0 end),0) as '31d'"
            + " from tb_player m inner join ( "
            + "   select t.account,t.approval_time create_time,t.actual_amount amount from tb_deposit_order t where t.ok_status = 1 and date_format(t.approval_time,'%Y-%m')=?2"
            + "   union all "
            + "   select t.account,t.create_time, t.amount from tb_manual_order t where t.type = 'deposit' and t.ok_status = 1 and date_format(t.create_time,'%Y-%m')=?2"
            + ")t on m.account = t.account where m.leader=?1 and date_format(m.reg_time,'%Y-%m')=?2")
    Map<String, Object> getSubRegMemberDepositAmount(String account, String month);

    /**
     * 数据对比，获取代理下线老用户存款人数
     *
     * @param acount
     * @param month
     * @return
     */
    @Query(nativeQuery = true, value = " select "
            + "         ifnull(count(distinct case when day(t.create_time) = 1 then t.account else null end),0) as '1d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 2 then t.account else null end),0) as '2d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 3 then t.account else null end),0) as '3d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 4 then t.account else null end),0) as '4d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 5 then t.account else null end),0) as '5d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 6 then t.account else null end),0) as '6d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 7 then t.account else null end),0) as '7d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 8 then t.account else null end),0) as '8d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 9 then t.account else null end),0) as '9d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 10 then t.account else null end),0) as '10d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 11 then t.account else null end),0) as '11d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 12 then t.account else null end),0) as '12d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 13 then t.account else null end),0) as '13d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 14 then t.account else null end),0) as '14d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 15 then t.account else null end),0) as '15d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 16 then t.account else null end),0) as '16d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 17 then t.account else null end),0) as '17d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 18 then t.account else null end),0) as '18d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 19 then t.account else null end),0) as '19d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 20 then t.account else null end),0) as '20d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 21 then t.account else null end),0) as '21d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 22 then t.account else null end),0) as '22d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 23 then t.account else null end),0) as '23d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 24 then t.account else null end),0) as '24d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 25 then t.account else null end),0) as '25d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 26 then t.account else null end),0) as '26d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 27 then t.account else null end),0) as '27d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 28 then t.account else null end),0) as '28d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 29 then t.account else null end),0) as '29d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 30 then t.account else null end),0) as '30d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 31 then t.account else null end),0) as '31d'"
            + " from tb_player m inner join ( "
            + "   select t.account,t.approval_time create_time from tb_deposit_order t where t.ok_status = 1 and date_format(t.approval_time,'%Y-%m')=?2"
            + "   union all "
            + "   select t.account,t.create_time from tb_manual_order t where t.type = 'deposit' and t.ok_status = 1 and date_format(t.create_time,'%Y-%m')=?2"
            + ")t on m.account = t.account where m.leader=?1 and date_format(m.reg_time,'%Y-%m')<?2")
    Map<String, Object> getOldSubMemberDeposit(String account, String month);

    /**
     * 数据对比，获取代理下线老用户存款金额
     *
     * @param acount
     * @param month
     * @return
     */
    @Query(nativeQuery = true, value = " select "
            + "         ifnull(sum(case when day(t.create_time) = 1 then t.amount else 0 end),0) as '1d',"
            + "         ifnull(sum(case when day(t.create_time) = 2 then t.amount else 0 end),0) as '2d',"
            + "         ifnull(sum(case when day(t.create_time) = 3 then t.amount else 0 end),0) as '3d',"
            + "         ifnull(sum(case when day(t.create_time) = 4 then t.amount else 0 end),0) as '4d',"
            + "         ifnull(sum(case when day(t.create_time) = 5 then t.amount else 0 end),0) as '5d',"
            + "         ifnull(sum(case when day(t.create_time) = 6 then t.amount else 0 end),0) as '6d',"
            + "         ifnull(sum(case when day(t.create_time) = 7 then t.amount else 0 end),0) as '7d',"
            + "         ifnull(sum(case when day(t.create_time) = 8 then t.amount else 0 end),0) as '8d',"
            + "         ifnull(sum(case when day(t.create_time) = 9 then t.amount else 0 end),0) as '9d',"
            + "         ifnull(sum(case when day(t.create_time) = 10 then t.amount else 0 end),0) as '10d',"
            + "         ifnull(sum(case when day(t.create_time) = 11 then t.amount else 0 end),0) as '11d',"
            + "         ifnull(sum(case when day(t.create_time) = 12 then t.amount else 0 end),0) as '12d',"
            + "         ifnull(sum(case when day(t.create_time) = 13 then t.amount else 0 end),0) as '13d',"
            + "         ifnull(sum(case when day(t.create_time) = 14 then t.amount else 0 end),0) as '14d',"
            + "         ifnull(sum(case when day(t.create_time) = 15 then t.amount else 0 end),0) as '15d',"
            + "         ifnull(sum(case when day(t.create_time) = 16 then t.amount else 0 end),0) as '16d',"
            + "         ifnull(sum(case when day(t.create_time) = 17 then t.amount else 0 end),0) as '17d',"
            + "         ifnull(sum(case when day(t.create_time) = 18 then t.amount else 0 end),0) as '18d',"
            + "         ifnull(sum(case when day(t.create_time) = 19 then t.amount else 0 end),0) as '19d',"
            + "         ifnull(sum(case when day(t.create_time) = 20 then t.amount else 0 end),0) as '20d',"
            + "         ifnull(sum(case when day(t.create_time) = 21 then t.amount else 0 end),0) as '21d',"
            + "         ifnull(sum(case when day(t.create_time) = 22 then t.amount else 0 end),0) as '22d',"
            + "         ifnull(sum(case when day(t.create_time) = 23 then t.amount else 0 end),0) as '23d',"
            + "         ifnull(sum(case when day(t.create_time) = 24 then t.amount else 0 end),0) as '24d',"
            + "         ifnull(sum(case when day(t.create_time) = 25 then t.amount else 0 end),0) as '25d',"
            + "         ifnull(sum(case when day(t.create_time) = 26 then t.amount else 0 end),0) as '26d',"
            + "         ifnull(sum(case when day(t.create_time) = 27 then t.amount else 0 end),0) as '27d',"
            + "         ifnull(sum(case when day(t.create_time) = 28 then t.amount else 0 end),0) as '28d',"
            + "         ifnull(sum(case when day(t.create_time) = 29 then t.amount else 0 end),0) as '29d',"
            + "         ifnull(sum(case when day(t.create_time) = 30 then t.amount else 0 end),0) as '30d',"
            + "         ifnull(sum(case when day(t.create_time) = 31 then t.amount else 0 end),0) as '31d'"
            + " from tb_player m inner join ( "
            + "   select t.account,t.approval_time create_time,t.actual_amount amount from tb_deposit_order t where t.ok_status = 1 and date_format(t.approval_time,'%Y-%m')=?2"
            + "   union all "
            + "   select t.account,t.create_time, t.amount from tb_manual_order t where t.type = 'deposit' and t.ok_status = 1 and date_format(t.create_time,'%Y-%m')=?2"
            + ")t on m.account = t.account where m.leader=?1 and date_format(m.reg_time,'%Y-%m')<?2")
    Map<String, Object> getOldSubMemberDepositAmount(String account, String month);

    /**
     * 数据对比，获取代理下线首存人数(当日注册且充值)
     */
    @Query(nativeQuery = true, value = " select "
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 1 then t.account else null end),0) as '1d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 2 then t.account else null end),0) as '2d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 3 then t.account else null end),0) as '3d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 4 then t.account else null end),0) as '4d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 5 then t.account else null end),0) as '5d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 6 then t.account else null end),0) as '6d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 7 then t.account else null end),0) as '7d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 8 then t.account else null end),0) as '8d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 9 then t.account else null end),0) as '9d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 10 then t.account else null end),0) as '10d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 11 then t.account else null end),0) as '11d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 12 then t.account else null end),0) as '12d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 13 then t.account else null end),0) as '13d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 14 then t.account else null end),0) as '14d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 15 then t.account else null end),0) as '15d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 16 then t.account else null end),0) as '16d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 17 then t.account else null end),0) as '17d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 18 then t.account else null end),0) as '18d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 19 then t.account else null end),0) as '19d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 20 then t.account else null end),0) as '20d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 21 then t.account else null end),0) as '21d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 22 then t.account else null end),0) as '22d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 23 then t.account else null end),0) as '23d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 24 then t.account else null end),0) as '24d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 25 then t.account else null end),0) as '25d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 26 then t.account else null end),0) as '26d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 27 then t.account else null end),0) as '27d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 28 then t.account else null end),0) as '28d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 29 then t.account else null end),0) as '29d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 30 then t.account else null end),0) as '30d',"
            + "         ifnull(count(distinct case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 31 then t.account else null end),0) as '31d'"
            + " from tb_player m inner join ( "
            + "   select t.account,t.approval_time create_time from tb_deposit_order t where t.ok_status = 1 and date_format(t.approval_time,'%Y-%m')=?2"
            + "   union all "
            + "   select t.account,t.create_time from tb_manual_order t where t.type = 'deposit' and t.ok_status = 1 and date_format(t.create_time,'%Y-%m')=?2"
            + ")t on m.account = t.account where m.leader=?1 and date_format(m.reg_time,'%Y-%m')=?2")
    Map<String, Object> getSubMemberFirstDeposit(String account, String month);

    /**
     * 数据对比，获取代理下线老用户存款金额
     */
    @Query(nativeQuery = true, value = " select "
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 1 then t.amount else 0 end),0) as '1d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 2 then t.amount else 0 end),0) as '2d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 3 then t.amount else 0 end),0) as '3d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 4 then t.amount else 0 end),0) as '4d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 5 then t.amount else 0 end),0) as '5d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 6 then t.amount else 0 end),0) as '6d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 7 then t.amount else 0 end),0) as '7d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 8 then t.amount else 0 end),0) as '8d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 9 then t.amount else 0 end),0) as '9d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 10 then t.amount else 0 end),0) as '10d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 11 then t.amount else 0 end),0) as '11d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 12 then t.amount else 0 end),0) as '12d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 13 then t.amount else 0 end),0) as '13d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 14 then t.amount else 0 end),0) as '14d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 15 then t.amount else 0 end),0) as '15d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 16 then t.amount else 0 end),0) as '16d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 17 then t.amount else 0 end),0) as '17d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 18 then t.amount else 0 end),0) as '18d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 19 then t.amount else 0 end),0) as '19d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 20 then t.amount else 0 end),0) as '20d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 21 then t.amount else 0 end),0) as '21d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 22 then t.amount else 0 end),0) as '22d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 23 then t.amount else 0 end),0) as '23d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 24 then t.amount else 0 end),0) as '24d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 25 then t.amount else 0 end),0) as '25d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 26 then t.amount else 0 end),0) as '26d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 27 then t.amount else 0 end),0) as '27d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 28 then t.amount else 0 end),0) as '28d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 29 then t.amount else 0 end),0) as '29d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 30 then t.amount else 0 end),0) as '30d',"
            + "         ifnull(sum(case when day(t.create_time)=day(m.reg_time) and day(t.create_time) = 31 then t.amount else 0 end),0) as '31d'"
            + " from tb_player m inner join ( "
            + "   select t.account,t.approval_time create_time,t.actual_amount amount from tb_deposit_order t where t.ok_status = 1 and date_format(t.approval_time,'%Y-%m')=?2"
            + "   union all "
            + "   select t.account,t.create_time, t.amount from tb_manual_order t where t.type = 'deposit' and t.ok_status = 1 and date_format(t.create_time,'%Y-%m')=?2"
            + ")t on m.account = t.account where m.leader=?1 and date_format(m.reg_time,'%Y-%m')=?2")
    Map<String, Object> getSubMemberFirstDepositAmount(String account, String month);

    /**
     * 数据对比，获取代理下线存款人数
     *
     * @param acount
     * @param month
     * @return
     */
    @Query(nativeQuery = true, value = " select "
            + "         ifnull(count(distinct case when day(t.create_time) = 1 then t.account else null end),0) as '1d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 2 then t.account else null end),0) as '2d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 3 then t.account else null end),0) as '3d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 4 then t.account else null end),0) as '4d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 5 then t.account else null end),0) as '5d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 6 then t.account else null end),0) as '6d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 7 then t.account else null end),0) as '7d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 8 then t.account else null end),0) as '8d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 9 then t.account else null end),0) as '9d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 10 then t.account else null end),0) as '10d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 11 then t.account else null end),0) as '11d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 12 then t.account else null end),0) as '12d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 13 then t.account else null end),0) as '13d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 14 then t.account else null end),0) as '14d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 15 then t.account else null end),0) as '15d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 16 then t.account else null end),0) as '16d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 17 then t.account else null end),0) as '17d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 18 then t.account else null end),0) as '18d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 19 then t.account else null end),0) as '19d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 20 then t.account else null end),0) as '20d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 21 then t.account else null end),0) as '21d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 22 then t.account else null end),0) as '22d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 23 then t.account else null end),0) as '23d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 24 then t.account else null end),0) as '24d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 25 then t.account else null end),0) as '25d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 26 then t.account else null end),0) as '26d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 27 then t.account else null end),0) as '27d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 28 then t.account else null end),0) as '28d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 29 then t.account else null end),0) as '29d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 30 then t.account else null end),0) as '30d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 31 then t.account else null end),0) as '31d'"
            + " from tb_player m inner join ( "
            + "   select t.account,t.approval_time create_time from tb_deposit_order t where t.ok_status = 1 and date_format(t.approval_time,'%Y-%m')=?2"
            + "   union all "
            + "   select t.account,t.create_time from tb_manual_order t where t.type = 'deposit' and t.ok_status = 1 and date_format(t.create_time,'%Y-%m')=?2"
            + ")t on m.account = t.account where m.leader=?1")
    Map<String, Object> getSubMemberDeposit(String account, String month);

    /**
     * 数据对比，获取代理下线用户存款总额
     *
     * @param account
     * @param month
     * @return
     */
    @Query(nativeQuery = true, value = " select "
            + "         ifnull(sum(case when day(t.create_time) = 1 then t.amount else 0 end),0) as '1d',"
            + "         ifnull(sum(case when day(t.create_time) = 2 then t.amount else 0 end),0) as '2d',"
            + "         ifnull(sum(case when day(t.create_time) = 3 then t.amount else 0 end),0) as '3d',"
            + "         ifnull(sum(case when day(t.create_time) = 4 then t.amount else 0 end),0) as '4d',"
            + "         ifnull(sum(case when day(t.create_time) = 5 then t.amount else 0 end),0) as '5d',"
            + "         ifnull(sum(case when day(t.create_time) = 6 then t.amount else 0 end),0) as '6d',"
            + "         ifnull(sum(case when day(t.create_time) = 7 then t.amount else 0 end),0) as '7d',"
            + "         ifnull(sum(case when day(t.create_time) = 8 then t.amount else 0 end),0) as '8d',"
            + "         ifnull(sum(case when day(t.create_time) = 9 then t.amount else 0 end),0) as '9d',"
            + "         ifnull(sum(case when day(t.create_time) = 10 then t.amount else 0 end),0) as '10d',"
            + "         ifnull(sum(case when day(t.create_time) = 11 then t.amount else 0 end),0) as '11d',"
            + "         ifnull(sum(case when day(t.create_time) = 12 then t.amount else 0 end),0) as '12d',"
            + "         ifnull(sum(case when day(t.create_time) = 13 then t.amount else 0 end),0) as '13d',"
            + "         ifnull(sum(case when day(t.create_time) = 14 then t.amount else 0 end),0) as '14d',"
            + "         ifnull(sum(case when day(t.create_time) = 15 then t.amount else 0 end),0) as '15d',"
            + "         ifnull(sum(case when day(t.create_time) = 16 then t.amount else 0 end),0) as '16d',"
            + "         ifnull(sum(case when day(t.create_time) = 17 then t.amount else 0 end),0) as '17d',"
            + "         ifnull(sum(case when day(t.create_time) = 18 then t.amount else 0 end),0) as '18d',"
            + "         ifnull(sum(case when day(t.create_time) = 19 then t.amount else 0 end),0) as '19d',"
            + "         ifnull(sum(case when day(t.create_time) = 20 then t.amount else 0 end),0) as '20d',"
            + "         ifnull(sum(case when day(t.create_time) = 21 then t.amount else 0 end),0) as '21d',"
            + "         ifnull(sum(case when day(t.create_time) = 22 then t.amount else 0 end),0) as '22d',"
            + "         ifnull(sum(case when day(t.create_time) = 23 then t.amount else 0 end),0) as '23d',"
            + "         ifnull(sum(case when day(t.create_time) = 24 then t.amount else 0 end),0) as '24d',"
            + "         ifnull(sum(case when day(t.create_time) = 25 then t.amount else 0 end),0) as '25d',"
            + "         ifnull(sum(case when day(t.create_time) = 26 then t.amount else 0 end),0) as '26d',"
            + "         ifnull(sum(case when day(t.create_time) = 27 then t.amount else 0 end),0) as '27d',"
            + "         ifnull(sum(case when day(t.create_time) = 28 then t.amount else 0 end),0) as '28d',"
            + "         ifnull(sum(case when day(t.create_time) = 29 then t.amount else 0 end),0) as '29d',"
            + "         ifnull(sum(case when day(t.create_time) = 30 then t.amount else 0 end),0) as '30d',"
            + "         ifnull(sum(case when day(t.create_time) = 31 then t.amount else 0 end),0) as '31d'"
            + " from tb_player m inner join ( "
            + "   select t.account,t.approval_time create_time,t.actual_amount amount from tb_deposit_order t where t.ok_status = 1 and date_format(t.approval_time,'%Y-%m')=?2"
            + "   union all "
            + "   select t.account,t.create_time, t.amount from tb_manual_order t where t.type = 'deposit' and t.ok_status = 1 and date_format(t.create_time,'%Y-%m')=?2"
            + ")t on m.account = t.account where m.leader=?1")
    Map<String, Object> getSubMemberDepositAmount(String account, String month);

    /**
     * 获取代理下线取款人数
     *
     * @param acount
     * @param month
     * @return
     */
    @Query(nativeQuery = true, value = " select "
            + "         ifnull(count(distinct case when day(t.create_time) = 1 then t.account else null end),0) as '1d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 2 then t.account else null end),0) as '2d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 3 then t.account else null end),0) as '3d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 4 then t.account else null end),0) as '4d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 5 then t.account else null end),0) as '5d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 6 then t.account else null end),0) as '6d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 7 then t.account else null end),0) as '7d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 8 then t.account else null end),0) as '8d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 9 then t.account else null end),0) as '9d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 10 then t.account else null end),0) as '10d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 11 then t.account else null end),0) as '11d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 12 then t.account else null end),0) as '12d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 13 then t.account else null end),0) as '13d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 14 then t.account else null end),0) as '14d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 15 then t.account else null end),0) as '15d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 16 then t.account else null end),0) as '16d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 17 then t.account else null end),0) as '17d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 18 then t.account else null end),0) as '18d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 19 then t.account else null end),0) as '19d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 20 then t.account else null end),0) as '20d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 21 then t.account else null end),0) as '21d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 22 then t.account else null end),0) as '22d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 23 then t.account else null end),0) as '23d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 24 then t.account else null end),0) as '24d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 25 then t.account else null end),0) as '25d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 26 then t.account else null end),0) as '26d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 27 then t.account else null end),0) as '27d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 28 then t.account else null end),0) as '28d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 29 then t.account else null end),0) as '29d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 30 then t.account else null end),0) as '30d',"
            + "         ifnull(count(distinct case when day(t.create_time) = 31 then t.account else null end),0) as '31d'"
            + " from tb_player m inner join ( "
            + "   select t.account,t.approval_time create_time from tb_draw_order t where t.ok_status = 1 and date_format(t.approval_time,'%Y-%m')=?2"
            + "   union all "
            + "   select t.account,t.create_time from tb_manual_order t where t.type = 'draw' and t.ok_status = 1 and date_format(t.create_time,'%Y-%m')=?2"
            + ")t on m.account = t.account where m.leader=?1")
    Map<String, Object> getSubMemberDraw(String account, String month);

    /**
     * 获取代理下线取款金额
     *
     * @param account
     * @param month
     * @return
     */
    @Query(nativeQuery = true, value = " select "
            + "         ifnull(sum(case when day(t.create_time) = 1 then t.amount else 0 end),0) as '1d',"
            + "         ifnull(sum(case when day(t.create_time) = 2 then t.amount else 0 end),0) as '2d',"
            + "         ifnull(sum(case when day(t.create_time) = 3 then t.amount else 0 end),0) as '3d',"
            + "         ifnull(sum(case when day(t.create_time) = 4 then t.amount else 0 end),0) as '4d',"
            + "         ifnull(sum(case when day(t.create_time) = 5 then t.amount else 0 end),0) as '5d',"
            + "         ifnull(sum(case when day(t.create_time) = 6 then t.amount else 0 end),0) as '6d',"
            + "         ifnull(sum(case when day(t.create_time) = 7 then t.amount else 0 end),0) as '7d',"
            + "         ifnull(sum(case when day(t.create_time) = 8 then t.amount else 0 end),0) as '8d',"
            + "         ifnull(sum(case when day(t.create_time) = 9 then t.amount else 0 end),0) as '9d',"
            + "         ifnull(sum(case when day(t.create_time) = 10 then t.amount else 0 end),0) as '10d',"
            + "         ifnull(sum(case when day(t.create_time) = 11 then t.amount else 0 end),0) as '11d',"
            + "         ifnull(sum(case when day(t.create_time) = 12 then t.amount else 0 end),0) as '12d',"
            + "         ifnull(sum(case when day(t.create_time) = 13 then t.amount else 0 end),0) as '13d',"
            + "         ifnull(sum(case when day(t.create_time) = 14 then t.amount else 0 end),0) as '14d',"
            + "         ifnull(sum(case when day(t.create_time) = 15 then t.amount else 0 end),0) as '15d',"
            + "         ifnull(sum(case when day(t.create_time) = 16 then t.amount else 0 end),0) as '16d',"
            + "         ifnull(sum(case when day(t.create_time) = 17 then t.amount else 0 end),0) as '17d',"
            + "         ifnull(sum(case when day(t.create_time) = 18 then t.amount else 0 end),0) as '18d',"
            + "         ifnull(sum(case when day(t.create_time) = 19 then t.amount else 0 end),0) as '19d',"
            + "         ifnull(sum(case when day(t.create_time) = 20 then t.amount else 0 end),0) as '20d',"
            + "         ifnull(sum(case when day(t.create_time) = 21 then t.amount else 0 end),0) as '21d',"
            + "         ifnull(sum(case when day(t.create_time) = 22 then t.amount else 0 end),0) as '22d',"
            + "         ifnull(sum(case when day(t.create_time) = 23 then t.amount else 0 end),0) as '23d',"
            + "         ifnull(sum(case when day(t.create_time) = 24 then t.amount else 0 end),0) as '24d',"
            + "         ifnull(sum(case when day(t.create_time) = 25 then t.amount else 0 end),0) as '25d',"
            + "         ifnull(sum(case when day(t.create_time) = 26 then t.amount else 0 end),0) as '26d',"
            + "         ifnull(sum(case when day(t.create_time) = 27 then t.amount else 0 end),0) as '27d',"
            + "         ifnull(sum(case when day(t.create_time) = 28 then t.amount else 0 end),0) as '28d',"
            + "         ifnull(sum(case when day(t.create_time) = 29 then t.amount else 0 end),0) as '29d',"
            + "         ifnull(sum(case when day(t.create_time) = 30 then t.amount else 0 end),0) as '30d',"
            + "         ifnull(sum(case when day(t.create_time) = 31 then t.amount else 0 end),0) as '31d'"
            + " from tb_player m inner join ( "
            + "   select t.account,t.approval_time create_time, amount from tb_draw_order t where t.ok_status = 1 and date_format(t.approval_time,'%Y-%m')=?2"
            + "   union all "
            + "   select t.account,t.create_time, t.amount from tb_manual_order t where t.type = 'draw' and t.ok_status = 1 and date_format(t.create_time,'%Y-%m')=?2"
            + ")t on m.account = t.account where m.leader=?1")
    Map<String, Object> getSubMemberDrawAmount(String account, String month);

    /**
     * 数据比对,获取代理下线投注人数
     *
     * @param account
     * @param month
     * @return
     */
    @Query(nativeQuery = true, value = " select "
            + "         coalesce(count(distinct case when day(t.create_time) = 1 then t.account else null end),0) as '1d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 2 then t.account else null end),0) as '2d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 3 then t.account else null end),0) as '3d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 4 then t.account else null end),0) as '4d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 5 then t.account else null end),0) as '5d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 6 then t.account else null end),0) as '6d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 7 then t.account else null end),0) as '7d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 8 then t.account else null end),0) as '8d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 9 then t.account else null end),0) as '9d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 10 then t.account else null end),0) as '10d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 11 then t.account else null end),0) as '11d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 12 then t.account else null end),0) as '12d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 13 then t.account else null end),0) as '13d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 14 then t.account else null end),0) as '14d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 15 then t.account else null end),0) as '15d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 16 then t.account else null end),0) as '16d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 17 then t.account else null end),0) as '17d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 18 then t.account else null end),0) as '18d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 19 then t.account else null end),0) as '19d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 20 then t.account else null end),0) as '20d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 21 then t.account else null end),0) as '21d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 22 then t.account else null end),0) as '22d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 23 then t.account else null end),0) as '23d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 24 then t.account else null end),0) as '24d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 25 then t.account else null end),0) as '25d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 26 then t.account else null end),0) as '26d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 27 then t.account else null end),0) as '27d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 28 then t.account else null end),0) as '28d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 29 then t.account else null end),0) as '29d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 30 then t.account else null end),0) as '30d',"
            + "         coalesce(count(distinct case when day(t.create_time) = 31 then t.account else null end),0) as '31d'"
            + " from tb_player m inner join tb_betting_order t on m.account = t.account where m.leader=?1 and date_format(t.create_time,'%Y-%m')=?2")
    Map<String, Object> getSubMemberBet(String account, String month);


    @Query(nativeQuery = true, value = " select "
            + "         coalesce(sum(case when day(t.create_time) = 1 then t.profit_amount else 0 end),0) as '1d',"
            + "         coalesce(sum(case when day(t.create_time) = 2 then t.profit_amount else 0 end),0) as '2d',"
            + "         coalesce(sum(case when day(t.create_time) = 3 then t.profit_amount else 0 end),0) as '3d',"
            + "         coalesce(sum(case when day(t.create_time) = 4 then t.profit_amount else 0 end),0) as '4d',"
            + "         coalesce(sum(case when day(t.create_time) = 5 then t.profit_amount else 0 end),0) as '5d',"
            + "         coalesce(sum(case when day(t.create_time) = 6 then t.profit_amount else 0 end),0) as '6d',"
            + "         coalesce(sum(case when day(t.create_time) = 7 then t.profit_amount else 0 end),0) as '7d',"
            + "         coalesce(sum(case when day(t.create_time) = 8 then t.profit_amount else 0 end),0) as '8d',"
            + "         coalesce(sum(case when day(t.create_time) = 9 then t.profit_amount else 0 end),0) as '9d',"
            + "         coalesce(sum(case when day(t.create_time) = 10 then t.profit_amount else 0 end),0) as '10d',"
            + "         coalesce(sum(case when day(t.create_time) = 11 then t.profit_amount else 0 end),0) as '11d',"
            + "         coalesce(sum(case when day(t.create_time) = 12 then t.profit_amount else 0 end),0) as '12d',"
            + "         coalesce(sum(case when day(t.create_time) = 13 then t.profit_amount else 0 end),0) as '13d',"
            + "         coalesce(sum(case when day(t.create_time) = 14 then t.profit_amount else 0 end),0) as '14d',"
            + "         coalesce(sum(case when day(t.create_time) = 15 then t.profit_amount else 0 end),0) as '15d',"
            + "         coalesce(sum(case when day(t.create_time) = 16 then t.profit_amount else 0 end),0) as '16d',"
            + "         coalesce(sum(case when day(t.create_time) = 17 then t.profit_amount else 0 end),0) as '17d',"
            + "         coalesce(sum(case when day(t.create_time) = 18 then t.profit_amount else 0 end),0) as '18d',"
            + "         coalesce(sum(case when day(t.create_time) = 19 then t.profit_amount else 0 end),0) as '19d',"
            + "         coalesce(sum(case when day(t.create_time) = 20 then t.profit_amount else 0 end),0) as '20d',"
            + "         coalesce(sum(case when day(t.create_time) = 21 then t.profit_amount else 0 end),0) as '21d',"
            + "         coalesce(sum(case when day(t.create_time) = 22 then t.profit_amount else 0 end),0) as '22d',"
            + "         coalesce(sum(case when day(t.create_time) = 23 then t.profit_amount else 0 end),0) as '23d',"
            + "         coalesce(sum(case when day(t.create_time) = 24 then t.profit_amount else 0 end),0) as '24d',"
            + "         coalesce(sum(case when day(t.create_time) = 25 then t.profit_amount else 0 end),0) as '25d',"
            + "         coalesce(sum(case when day(t.create_time) = 26 then t.profit_amount else 0 end),0) as '26d',"
            + "         coalesce(sum(case when day(t.create_time) = 27 then t.profit_amount else 0 end),0) as '27d',"
            + "         coalesce(sum(case when day(t.create_time) = 28 then t.profit_amount else 0 end),0) as '28d',"
            + "         coalesce(sum(case when day(t.create_time) = 29 then t.profit_amount else 0 end),0) as '29d',"
            + "         coalesce(sum(case when day(t.create_time) = 30 then t.profit_amount else 0 end),0) as '30d',"
            + "         coalesce(sum(case when day(t.create_time) = 31 then t.profit_amount else 0 end),0) as '31d'"
            + " from tb_player m inner join tb_betting_order t on m.account = t.account where m.leader=?1 and date_format(t.create_time,'%Y-%m')=?2")
    Map<String, Object> getSubMemberBetProfitAmount(String account, String month);

    /**
     * 数据比对,获取代理下线投注金额
     */
    @Query(nativeQuery = true, value = " select "
            + "         coalesce(sum(case when day(t.create_time) = 1 then t.bet_amount else 0 end),0) as '1d',"
            + "         coalesce(sum(case when day(t.create_time) = 2 then t.bet_amount else 0 end),0) as '2d',"
            + "         coalesce(sum(case when day(t.create_time) = 3 then t.bet_amount else 0 end),0) as '3d',"
            + "         coalesce(sum(case when day(t.create_time) = 4 then t.bet_amount else 0 end),0) as '4d',"
            + "         coalesce(sum(case when day(t.create_time) = 5 then t.bet_amount else 0 end),0) as '5d',"
            + "         coalesce(sum(case when day(t.create_time) = 6 then t.bet_amount else 0 end),0) as '6d',"
            + "         coalesce(sum(case when day(t.create_time) = 7 then t.bet_amount else 0 end),0) as '7d',"
            + "         coalesce(sum(case when day(t.create_time) = 8 then t.bet_amount else 0 end),0) as '8d',"
            + "         coalesce(sum(case when day(t.create_time) = 9 then t.bet_amount else 0 end),0) as '9d',"
            + "         coalesce(sum(case when day(t.create_time) = 10 then t.bet_amount else 0 end),0) as '10d',"
            + "         coalesce(sum(case when day(t.create_time) = 11 then t.bet_amount else 0 end),0) as '11d',"
            + "         coalesce(sum(case when day(t.create_time) = 12 then t.bet_amount else 0 end),0) as '12d',"
            + "         coalesce(sum(case when day(t.create_time) = 13 then t.bet_amount else 0 end),0) as '13d',"
            + "         coalesce(sum(case when day(t.create_time) = 14 then t.bet_amount else 0 end),0) as '14d',"
            + "         coalesce(sum(case when day(t.create_time) = 15 then t.bet_amount else 0 end),0) as '15d',"
            + "         coalesce(sum(case when day(t.create_time) = 16 then t.bet_amount else 0 end),0) as '16d',"
            + "         coalesce(sum(case when day(t.create_time) = 17 then t.bet_amount else 0 end),0) as '17d',"
            + "         coalesce(sum(case when day(t.create_time) = 18 then t.bet_amount else 0 end),0) as '18d',"
            + "         coalesce(sum(case when day(t.create_time) = 19 then t.bet_amount else 0 end),0) as '19d',"
            + "         coalesce(sum(case when day(t.create_time) = 20 then t.bet_amount else 0 end),0) as '20d',"
            + "         coalesce(sum(case when day(t.create_time) = 21 then t.bet_amount else 0 end),0) as '21d',"
            + "         coalesce(sum(case when day(t.create_time) = 22 then t.bet_amount else 0 end),0) as '22d',"
            + "         coalesce(sum(case when day(t.create_time) = 23 then t.bet_amount else 0 end),0) as '23d',"
            + "         coalesce(sum(case when day(t.create_time) = 24 then t.bet_amount else 0 end),0) as '24d',"
            + "         coalesce(sum(case when day(t.create_time) = 25 then t.bet_amount else 0 end),0) as '25d',"
            + "         coalesce(sum(case when day(t.create_time) = 26 then t.bet_amount else 0 end),0) as '26d',"
            + "         coalesce(sum(case when day(t.create_time) = 27 then t.bet_amount else 0 end),0) as '27d',"
            + "         coalesce(sum(case when day(t.create_time) = 28 then t.bet_amount else 0 end),0) as '28d',"
            + "         coalesce(sum(case when day(t.create_time) = 29 then t.bet_amount else 0 end),0) as '29d',"
            + "         coalesce(sum(case when day(t.create_time) = 30 then t.bet_amount else 0 end),0) as '30d',"
            + "         coalesce(sum(case when day(t.create_time) = 31 then t.bet_amount else 0 end),0) as '31d'"
            + " from tb_player m inner join tb_betting_order t on m.account = t.account where m.leader=?1 and date_format(t.create_time,'%Y-%m')=?2")
    Map<String, Object> getSubMemberBetAmount(String account, String month);


    @Query(nativeQuery = true,
            value = " select t.ok_status as okStatus, t.account as account, t.platform as apiCode, t.TRANSACTION_ID as orderNo," +
                    "   t.profit_amount as profitAmount, t.bet_amount as betAmount, t.bet_total as betTotal, t.game_name as gameName," +
                    "   t.create_time as createTime"
                    + " from"
                    + "    tb_betting_order t inner join tb_player t1 on t.account = t1.account and t1.type=1"
                    + " where 1=1 "
                    + "   and if(ifnull(?1,'') != '', t1.leader=?1, 1=1)"
                    + "   and if(ifnull(?2,'') != '', t.account=?2, 1=1)"
                    + "   and if(ifnull(?3,'') != '', t.platform=?3, 1=1)"
                    + "   and if(ifnull(?4,'') != '', t.create_time>=?4, 1=1)"
                    + "   and if(ifnull(?5,'') != '', t.create_time<=?5, 1=1)"
                    + " order by "
                    + "    t.create_time desc,"
                    + "    t.account desc",
            countQuery = "select count(account) from ( "
                    + " select t.account "
                    + " from"
                    + "     tb_betting_order t inner join tb_player t1 on t.account = t1.account and t1.type=1 "
                    + " where 1=1 "
                    + "   and if(ifnull(?1,'') != '', t1.leader=?1, 1=1)"
                    + "   and if(ifnull(?2,'') != '', t.account=?2, 1=1)"
                    + "   and if(ifnull(?3,'') != '', t.platform=?3, 1=1)"
                    + "   and if(ifnull(?4,'') != '', t.create_time>=?4, 1=1)"
                    + "   and if(ifnull(?5,'') != '', t.create_time<=?5, 1=1)"
                    + ")t")
    Page<Map<String, Object>> findAll(String leader, String member, String apiCode, String startTime, String endTime, Pageable pageable);

    /**
     * 获取代理结算时各游戏的返佣比列
     */
    @Query(nativeQuery = true, value = "select"
            + " platform as apiCode, game_type as gameType, commission_point as percentage "
            + " from tb_commission_percentage  "
            + " where serial_no = (select max(serial_no) from tb_commission_order where account = ?1 and `month` = ?2) and leader = ?1 and `month`= ?2")
    List<Map<String, Object>> getCommisionSettledScheme(String account, String month);

    /**
     * 获取代理返佣编号
     *
     * @param account
     * @return
     */
    @Query(nativeQuery = true, value = "select commission_code from tb_player where account = ?1")
    String getAgentCommissionCode(String account);

    /**
     * 获取本期代理总盈亏以及有效会员数
     */
    @Query(nativeQuery = true, value = "select "
            + "   count(distinct b.account) memNum,"
            + "  ifnull(sum(b.profit_amount),0) profitTotalAmount"
            + " from "
            + "   tb_player t "
            + " left join tb_betting_order b on t.account = b.account"
            + " left join (select o.account, o.platform, o.game_type, max(o.approval_time) last_approval_time from tb_commission_detail o group by o.account, o.platform, o.game_type) c"
            + " on t.account=c.account and b.platform=c.platform and b.game_type=c.game_type"
            + " where if(c.last_approval_time is not null, b.approval_time>c.last_approval_time,1=1) and b.create_time<=?2 and t.leader=?1")
    Map<String, Object> getAgentTotalProfitAndVaildMember(String account, Timestamp commissionUptoDate);


    /**
     * 根据代理返佣方案各个游戏的具体返佣比列
     */
    @Query(nativeQuery = true, value = "select c.platform apiCode, c.game_type gameType, ifnull(c.commission_point,0) percentage from tb_commission_cfg c "
            + "inner join ("
            + "     select c.scheme_code,c.platform, c.game_type, min(c.id) id, max(c.profit_amount)profit_amount, max(c.member_num) member_num"
            + "     from tb_commission_cfg c where c.scheme_code=?1 and c.profit_amount<=?2 and c.member_num <= ?3 "
            + "     group by c.scheme_code,c.platform, c.game_type"
            + ")c1 "
            + " on c.scheme_code = c1.scheme_code and c.platform = c1.platform and c.game_type=c1.game_type "
            + " and c.profit_amount = c1.profit_amount and c.member_num = c1.member_num"
            + "")
    List<Map<String, Object>> getCommissionSchemeCfg(String commissionCode, BigDecimal totalProfitAmount, Integer totalVaildMember);

    /**
     * 默认数据，防止页面展示空白
     * @param commissionCode
     * @return
     */
    @Query(nativeQuery = true, value = "select c.platform apiCode, c.game_type gameType, 0 as 'percentage' from tb_commission_cfg c "
            + " where c.scheme_code=?1 "
            + " group by c.platform, c.game_type"
            + "")
    List<Map<String, Object>> getDefaultCommissionSchemeCfg(String commissionCode);
}
