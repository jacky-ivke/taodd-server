package com.esports.basic.dao.db1;

import com.esports.basic.bean.db1.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AppVersionDao extends JpaRepository<AppVersion, Long>, JpaSpecificationExecutor<AppVersion> {

    @Query(value = "select * from tb_app_version where ok_status =1 and if(ifnull(?1,'') <> '',apk_type=?1,1=1) order by app_version desc limit 1", nativeQuery = true)
    AppVersion getCurrentReleaseVersion(String apkType);
}
