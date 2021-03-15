 package com.esports.center.channel.dao.db1;

 import com.esports.center.channel.bean.db1.DrawChannel;
 import org.springframework.data.jpa.repository.JpaRepository;
 import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
 import org.springframework.stereotype.Repository;

 import java.util.List;

 @Repository
public interface DrawChannelDao extends JpaRepository<DrawChannel,Long>,JpaSpecificationExecutor<DrawChannel>{

    List<DrawChannel> findByChannelCode(String channelCode);
}
