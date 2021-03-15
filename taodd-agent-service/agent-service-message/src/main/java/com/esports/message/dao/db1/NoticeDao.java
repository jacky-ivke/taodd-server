package com.esports.message.dao.db1;


import com.esports.message.bean.db1.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface NoticeDao extends JpaRepository<Notice, Long>, JpaSpecificationExecutor<Notice> {


    /**
     * 获取代理未读站内消息
     */
    @Query(nativeQuery = true, value = "select " +
            " count(id) " +
            " from (" +
            "   select id, ifnull(accounts, ?1) accounts, start_time, end_time from tb_notice t " +
            "   where (find_in_set(?2,identity) and type = 1) " +
            "       or (find_in_set(?1,t.accounts) and find_in_set(?2,identity) and find_in_set(?3, platform) and type = 3)" +
            ") t " +
            " where not exists(select message_id from tb_sys_message t1 where t.id = t1.message_id and find_in_set(t1.account,t.accounts))" +
            " and t.start_time>=?4 and t.end_time>=?5 " +
            " and start_time<=curdate() and end_time>=curdate()")
    Integer getUnReadNoticeMsg(String account, String identity, Integer source, Timestamp startTime, Timestamp endTime);

    @Query(nativeQuery = true, value = "select " +
            " count(id) " +
            " from tb_notice t " +
            " where not exists(select message_id from tb_sys_message t1 where t.id = t1.message_id and t1.account=?1)" +
            " and t.type = 7 and find_in_set(?2, platform) and start_time >= ?3 and end_time >=?4" +
            " and start_time<=curdate() and end_time>=curdate()")
    Integer getUnReadAdviseMsg(String account, Integer source, Timestamp startTime, Timestamp endTime);

}
