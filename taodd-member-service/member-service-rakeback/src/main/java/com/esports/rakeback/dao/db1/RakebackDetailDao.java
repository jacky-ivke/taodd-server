 package com.esports.rakeback.dao.db1;

import com.esports.rakeback.bean.db1.RakebackDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

 @Repository
 public interface RakebackDetailDao extends JpaRepository<RakebackDetail,Long>,JpaSpecificationExecutor<RakebackDetail>{

     @Query(nativeQuery=true, value=
         "select "
             + "  count(t.account) betNum,"
             + "  sum(t.bet_amount) betAmount,"
             + "  sum(t.profit_amount) profitAmount,"
             + "  max(t.approval_time) approvalTime,"
             + "  t.platform platform,"
             + "  t.game_type gameType "
             +" from "
             +"   tb_betting_order t "
             +" left join("
             +"   select o.account, o.platform, o.game_type, max(o.approval_time)approval_time,max(o.create_time)create_time from tb_rakeback_detail o  group by o.account, o.platform, o.game_type"
             +" )t1 on t.account = t1.account and t.platform=t1.platform and t.game_type = t1.game_type"
             + " where t.account=?1 and if(t1.approval_time is not null, t.approval_time>t1.approval_time, 1=1)"
             +" group by t.account, t.platform, t.game_type ")
     List<Map<String,Object>> getMemberRakeDetail(String account);

     @Query(nativeQuery=true,
             value=" select "
                     + "	coalesce(sum(t.bet_amount),0) betTotalAmount, "
                     + " coalesce(sum(truncate(t.bet_amount * (t.point/100), 2)),0) rakeTotalAmount"
                     + " from "
                     + "		tb_rakeback_detail t"
                     + " where "
                     + "		t.account=?1 "
                     + "		and if(?2 != '', t.platform=?2, 1=1)"
                     + "		and if(?3 != '', t.game_type=?3,1=1)"
                     + "		and if(?4 != '', t.create_time>=?4,1=1)"
                     + "		and if(?5 != '', t.create_time<=?5,1=1)")
     Map<String,Object> getWashSummary(String account, String apiCode, String gameType, String startTime, String endTime);



 }
