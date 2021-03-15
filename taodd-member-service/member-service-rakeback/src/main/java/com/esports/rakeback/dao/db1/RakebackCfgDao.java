package com.esports.rakeback.dao.db1;

import com.esports.rakeback.bean.db1.RakebackCfg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RakebackCfgDao extends JpaRepository<RakebackCfg, Long> {

    /**
     * 获取玩家各API返水比例
     */
    @Query(nativeQuery = true, value = "select * from tb_rakeback_cfg where scheme_code=?1 and vip=?2 and point>=0")
    List<RakebackCfg> findBySchemeCodeAndVip(String schemeCode, Integer vip);

    /**
     * 获取玩家对应的API游戏的返水配置
     */
    @Query(nativeQuery = true, value = "select * from tb_rakeback_cfg where scheme_code=?1 and vip=?2 and point>0 and platform=?3 and game_type=?4")
    RakebackCfg getRakebackCfg(String schemeCode, Integer vip, String platform, String gameType);

	/**
	 * 获取默认各API游戏的反水比
	 */
    @Query(nativeQuery = true, value = "select c.platform platform, c.game_type gameType, 0 as 'point' from tb_rakeback_cfg c  where c.scheme_code=?1 group by c.platform, c.game_type")
    List<Map<String, Object>> getDefaultRakebackSchemeCfg(String schemeCode);
}
