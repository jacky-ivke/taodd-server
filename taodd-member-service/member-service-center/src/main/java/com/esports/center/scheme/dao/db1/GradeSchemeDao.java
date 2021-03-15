package com.esports.center.scheme.dao.db1;

import com.esports.center.scheme.bean.db1.GradeScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeSchemeDao extends JpaRepository<GradeScheme,Long>,JpaSpecificationExecutor<GradeScheme>{

	GradeScheme findByOkStatusAndSetDefault(Integer okStatus, Boolean setDefault);
}
