package com.esports.message.dao.db1;

import com.esports.message.bean.db1.SysMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SysMessageDao extends JpaRepository<SysMessage, Long>, JpaSpecificationExecutor<SysMessage> {

    @Modifying
    @Query("update SysMessage set okStatus=?2 where messageId=?1 and account=?3")
    void updateMessageStatus(Long id, Integer okStatus, String account);

    SysMessage findByMessageIdAndAccount(Long messageId, String account);
}
