package com.esports.external.handler.xint.ibc;

public enum LanguageCode {
	CHN("cs", 1, "简体中文"),
	THN("ch", 2, "繁体中文"),
	ENG("en", 1, "英文"),
	JP("jp", 3, "日本語"),
	KR("ko", 4, "韩语"),
	VN("vn", 5,"越南语"),
	TH("th", 6,"泰语")
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
