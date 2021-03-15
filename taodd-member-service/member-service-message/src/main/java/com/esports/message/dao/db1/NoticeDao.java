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
     * 获取会员未读站内消息[群发消息、指定会员消息、指定层级消息、指定vip消息]
     */
    @Query(nativeQuery = true, value = "select " +
            " count(id) " +
            " from (" +
            "   select id, ifnull(accounts, ?1) accounts, start_time, end_time from tb_notice t " +
            "   where (find_in_set(?2,identity) and type = 1) " +
            "       or (find_in_set(?1,t.accounts) and find_in_set(?2,identity) and find_in_set(?3, platform) and type = 3)" +
            "       or (find_in_set(?4, t.vips) and type = 4) " +
            "       or (find_in_set(?5, t.grades) and type = 5)" +
            ") t " +
            " where not exists(select message_id from tb_sys_message t1 where t.id = t1.message_id and find_in_set(t1.account,t.accounts))" +
            " and t.start_time>=?6 and t.end_time>=?7 " +
            " and start_time<=curdate() and end_time>=curdate()")
    Integer getUnReadNoticeMsg(String account, String identity, Integer source, Integer vip, String grade, Timestamp startTime, Timestamp endTime);

    /**
     * 获取会员未读公告消息
     */
    @Query(nativeQuery = true, value = "select " +
            " count(id) " +
            " from tb_notice t " +
            " where not exists(select message_id from tb_sys_message t1 where t.id = t1.message_id and t1.account=?1)" +
            " and t.type = 7 and find_in_set(?2, platform) and start_time >= ?3 and end_time >=?4" +
            " and start_time<=curdate() and end_time>=curdate()")
    Integer getUnReadAdviseMsg(String account, Integer source, Timestamp startTime, Timestamp endTime);

}
