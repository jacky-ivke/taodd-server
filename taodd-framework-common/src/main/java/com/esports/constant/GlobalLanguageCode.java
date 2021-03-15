package com.esports.constant;

/**
 * 语言类型
 * @author jacky
 */
public enum GlobalLanguageCode {
	
	ZH("","");
	
	private String code;

	private String message;

	GlobalLanguageCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	public static String getRemarks(String code) {
		String remarks = "";
		if(null == code){
			return remarks;
		}
		for(GlobalLanguageCode enumType :GlobalLanguageCode.values()){
			 if(enumType.getCode().equals(code)){
				 return enumType.getMessage();
			 }
		 }
		 return remarks;
	}
}
