package com.esports.external.basic.dao.db1;

import com.esports.external.basic.bean.db1.ExternalApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExternalAppDao extends JpaRepository<ExternalApp,Long>, JpaSpecificationExecutor<ExternalApp> {

	@Query(nativeQuery=true, value="select * from tb_external_app where ok_status=1 order by priority")
    List<ExternalApp> getExternalApps();

    ExternalApp findByAppCodeAndOkStatus(String apiCode, Integer okStatus);
}
