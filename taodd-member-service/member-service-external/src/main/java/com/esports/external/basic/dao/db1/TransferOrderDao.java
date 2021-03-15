 package com.esports.external.basic.dao.db1;

 import com.esports.external.basic.bean.db1.TransferOrder;
 import org.springframework.data.jpa.repository.JpaRepository;
 import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
 import org.springframework.data.jpa.repository.Query;
 import org.springframework.stereotype.Repository;

 import java.util.List;

 @Repository
 public interface TransferOrderDao extends JpaRepository<TransferOrder,Long>,JpaSpecificationExecutor<TransferOrder>{

     
     @Query(nativeQuery=true,
         value="select (case when revenue='center' then expenditure else revenue end) apiCode from tb_transfer_order where account = ?1 and type='deposit' and ok_status=1 order by create_time desc limit 1")
     String getLastTransferPlat(String account);
     
     @Query(nativeQuery=true,
             value="select distinct (case when revenue='center' then expenditure else revenue end) from tb_transfer_order where account =?1 and ok_status=1")
     List<String> getTransferPlats(String account);
}
