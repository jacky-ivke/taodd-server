package com.esports.external.handler.xint.pt;

public enum LanguageCode {
	CHN("ZH-CN", 1, "简体中文"),
	THN("CH", 2, "繁体中文"),
	ENG("EN", 1, "英文"),
	JP("JP", 3, "日本語"),
	KR("KR", 4, "韩语"),
	VN("VN", 5,"越南语"),
	TH("TH", 6,"泰语"),
	ES("ES", 7,"西班牙语"),
	PT("PT", 8,"葡萄牙语"),
	FR("FR", 9,"法语"),
	DE("DE", 10,"德语"),
	IT("IT", 11,"意大利语"),
	RU("RU", 12,"俄语"),
	ID("ID", 13,"印尼语")
	;
	private String code;
	
	/**
	 * 统一语言编号
	 */
	private Integer global;
	
	private String msg;
	
	LanguageCode(String code, Integer global, String msg){
		this.code = code;
		this.global = global;
		this.msg = msg;
		
	}

	
	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public Integer getGlobal() {
		return global;
	}


	public void setGlobal(Integer global) {
		this.global = global;
	}


	public String getMsg() {
		return msg;
	}


	public void setMsg(String msg) {
		this.msg = msg;
	}

	public static String getLanguage(Integer lang) {
		if(null == lang) {
			return CHN.getCode();
		}
		//将全局设定的语言编号转为当前平台对应的语言
		
		//后期设计
		
		return CHN.getCode(); 
	}

}
