package com.esports.basic.dao.db1;

import com.esports.basic.bean.db1.IpBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IpBlacklistDao extends JpaRepository<IpBlacklist, Long>, JpaSpecificationExecutor<IpBlacklist>{

	@Query("select t from IpBlacklist t where t.ipFrom<=?1 and t.ipTo>=?2 and t.okStatus=1")
	List<IpBlacklist> findByIp(Long ipFrom, Long ipTo);
}
