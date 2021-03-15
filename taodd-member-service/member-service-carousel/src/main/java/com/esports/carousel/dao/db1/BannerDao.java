package com.esports.carousel.dao.db1;

import com.esports.carousel.bean.db1.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerDao extends JpaRepository<Banner, Long>, JpaSpecificationExecutor<Banner>{

    @Query(nativeQuery=true, value="select * from tb_banner where ok_status=1 and nav_code=?1 and find_in_set(?2, platform) order by prority")
    List<Banner> getBannersByNavCode(String navCode, Integer source);
}
