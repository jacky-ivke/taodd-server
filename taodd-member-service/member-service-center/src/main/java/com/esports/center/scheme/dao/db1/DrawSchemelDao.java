package com.esports.center.scheme.dao.db1;

import com.esports.center.scheme.bean.db1.DrawScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrawSchemelDao extends JpaRepository<DrawScheme,Long> {

	DrawScheme findBySchemeCodeAndOkStatus(String shemeCode, Integer okStatus);
}
