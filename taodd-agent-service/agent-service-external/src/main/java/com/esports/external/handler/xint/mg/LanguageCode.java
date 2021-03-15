package com.esports.external.handler.xint.mg;

public enum LanguageCode {
	CHN("zh_CN", 1, "简体中文")
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
