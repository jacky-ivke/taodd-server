package com.esports.basic.service;

import com.esports.basic.bean.db1.AppVersion;
import com.esports.basic.dao.db1.AppVersionDao;
import com.esports.basic.dto.AppVersionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppVersionManager {

	@Autowired
	private AppVersionDao appVersionDao;
	
	
	public AppVersionDto getCurrentReleaseVersion(String apkType) {
		AppVersionDto dto = null;
		AppVersion appVersion = appVersionDao.getCurrentReleaseVersion(apkType);
		if(null == appVersion) {
			return dto;
		}
		dto = new AppVersionDto();
		dto.setVersion(appVersion.getVersion());
		dto.setDownload(appVersion.getDownload());
		dto.setUpgradeMode(appVersion.getUpgradeMode());
		return dto;
	}
}
