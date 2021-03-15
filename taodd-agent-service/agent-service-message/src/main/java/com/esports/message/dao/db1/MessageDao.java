package com.esports.message.dao.db1;

import com.esports.message.bean.db1.MessageText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageDao extends JpaRepository<MessageText, Long>, JpaSpecificationExecutor<MessageText> {

    @Query(value = "select * from tb_message_text where type=?1 and find_in_set(?2, identity) and find_in_set(?3, platform) limit 1", nativeQuery = true)
    MessageText getMessageText(String type, String identity, Integer source);
}
