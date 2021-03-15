package com.esports.external.handler.xint.ag;

public enum LanguageCode {
	
	CHN("1",1, "简体中文")
	;
	/**
	 * 第三方平台自定义编号
	 */
	private String code;
	
	/**
	 * 统一语言编号
	 */
	private Integer global;
	
	/**
	 * 说明
	 */
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
		 for(LanguageCode code : LanguageCode.values()){
		 	//将全局设定的语言编号转为当前平台对应的语言
            if(code.getGlobal().equals(lang)){
                return code.getCode();
            }
        }
		return CHN.getCode(); 
	}

}
