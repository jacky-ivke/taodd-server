package com.esports.external.proxy.dao.db1;

import com.esports.external.proxy.bean.db1.ProxyConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ProxyConfigDao extends JpaRepository<ProxyConfig,Long>,JpaSpecificationExecutor<ProxyConfig>{

	@Query("from ProxyConfig where merchantType=?1 and apiCode=?2 and apiType=?3")
	ProxyConfig getConfig(String merchantType, String apiCode, String apiType);
}
