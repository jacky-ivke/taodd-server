package com.esports.log.dao.db1;

import com.esports.log.bean.db1.MemberPlayGameLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberPlayGameLogDao extends JpaRepository<MemberPlayGameLog, Long>, JpaSpecificationExecutor<MemberPlayGameLog>{

}
