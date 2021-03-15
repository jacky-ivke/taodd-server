package com.esports.basic.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppVersionDto implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * 版本编号
	 */
	private String version;
	
	/**
	 * 下载地址
	 */
	private String download;
	
	/**
	 * 升级模式
	 */
	private Integer upgradeMode;
}
