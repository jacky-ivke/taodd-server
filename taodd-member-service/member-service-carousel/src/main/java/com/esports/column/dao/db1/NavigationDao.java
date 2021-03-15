 package com.esports.column.dao.db1;

 import com.esports.column.bean.db1.Navigation;
 import org.springframework.data.jpa.repository.JpaRepository;
 import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
 import org.springframework.data.jpa.repository.Query;
 import org.springframework.stereotype.Repository;

 import java.util.List;

 @Repository
 public interface NavigationDao extends JpaRepository<Navigation, Long>, JpaSpecificationExecutor<Navigation>{

     @Query(nativeQuery=true, value="select * from tb_navigation where ok_status=1 and parent_id is null order by prority")
     List<Navigation> getTopNavs();

     Navigation findByNavCode(String navCode);

 }
