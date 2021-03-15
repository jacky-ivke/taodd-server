package com.esports.log.dao.db1;

import com.esports.log.bean.db1.AgentTradeLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentTradeLogDao extends JpaRepository<AgentTradeLog, Long>, JpaSpecificationExecutor<AgentTradeLog> {

    @Query(nativeQuery = true,
            value = " select t.* "
                    + " from"
                    + "    tb_agenttrade_log t "
                    + " inner join "
                    + "      (select t.account,t.order_no, t.second_type,max(t.create_time) create_time from tb_agenttrade_log t group by t.account,t.order_no,t.second_type) t1"
                    + " on t.account = t1.account and t.create_time = t1.create_time and t.order_no = t1.order_no and t.second_type = t1.second_type"
                    + " where 1=1 "
                    + "   and if(ifnull(?1,'') != '', t.wallet_type=?1, 1=1)"
                    + "   and if(ifnull(?2,'') != '', t.account=?2, 1=1)"
                    + "   and if(ifnull(?3,'') != '', t.ok_status=?3, 1=1)"
                    + "   and if(ifnull(?4,'') != '', t.second_type=?4, 1=1)"
                    + "   and if(ifnull(?5,'') != '', t.create_time>=?5, 1=1)"
                    + "   and if(ifnull(?6,'') != '', t.create_time<=?6, 1=1)"
                    + " order by "
                    + "    t.create_time desc,"
                    + "    t.account desc",
            countQuery = "select count(account) from ( "
                    + " select t.account "
                    + " from"
                    + "    tb_agenttrade_log t "
                    + " inner join "
                    + "      (select t.account,t.order_no, t.second_type,max(t.create_time) create_time from tb_agenttrade_log t group by t.account,t.order_no,t.second_type) t1"
                    + " on t.account = t1.account and t.create_time = t1.create_time and t.order_no = t1.order_no and t.second_type = t1.second_type"
                    + " where 1=1 "
                    + "   and if(ifnull(?1,'') != '', t.wallet_type=?1, 1=1)"
                    + "   and if(ifnull(?2,'') != '', t.account=?2, 1=1)"
                    + "   and if(ifnull(?3,'') != '', t.ok_status=?3, 1=1)"
                    + "   and if(ifnull(?4,'') != '', t.second_type=?4, 1=1)"
                    + "   and if(ifnull(?5,'') != '', t.create_time>=?5, 1=1)"
                    + "   and if(ifnull(?6,'') != '', t.create_time<=?6, 1=1)"
                    + ")t")
    Page<AgentTradeLog> findAll(String walletType, String account, Integer okStatus, String type, String startTime, String endTime, Pageable pageable);

}
