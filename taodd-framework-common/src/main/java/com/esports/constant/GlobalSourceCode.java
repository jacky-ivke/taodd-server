package com.esports.constant;

/**
 * 终端类型
 * @author jacky
 *
 */
public enum GlobalSourceCode {
	_PC("0", "PC端")
	,_H5("1", "H5")
	,_NATIVE("2", "Native")
	,_ANDROID("3", "Android")
	,_IOS("4", "iOS")
	,_HM("5", "HM")
	;
	private String code;

	private String message;

	GlobalSourceCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
