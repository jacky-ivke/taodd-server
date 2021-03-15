package com.esports.external.proxy.service;

import com.esports.external.proxy.bean.db1.ProxyConfig;
import com.esports.external.proxy.dao.db1.ProxyConfigDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProxyConfigManager{

	@Autowired
	private ProxyConfigDao proxyConfigDao;
	
	public ProxyConfig getConfig(String merchantType, String apiCode, String apiType) {
		return proxyConfigDao.getConfig(merchantType, apiCode, apiType);
	}
	
}
