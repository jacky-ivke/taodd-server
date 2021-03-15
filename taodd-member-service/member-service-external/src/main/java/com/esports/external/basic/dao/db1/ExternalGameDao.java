package com.esports.external.basic.dao.db1;

import com.esports.external.basic.bean.db1.ExternalGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalGameDao extends JpaRepository<ExternalGame, Long>, JpaSpecificationExecutor<ExternalGame> {

    ExternalGame findByApiCodeAndGameCode(String apiCode, String gameCode);
}
