 package com.esports.center.domain.service;

 import com.esports.center.domain.bean.db1.Domain;
 import com.esports.center.domain.dao.db1.DomainDao;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Service;

 @Service
public class DomainManager {

     @Autowired
     private DomainDao domainDao;
     
     public Domain getDefaultDomain(String type) {
         Boolean setDefault = Boolean.TRUE;
         Domain domain = domainDao.findByTypeAndSetDefault(type, setDefault);
         return domain;
     }
}
