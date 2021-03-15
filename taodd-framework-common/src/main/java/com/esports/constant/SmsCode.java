package com.esports.constant;

public enum SmsCode {

	_FREQUENT_SENDING(0, "发送频繁限制")
	
	,_FREQUENCY_LIMIT(2, "发送次数限制")
	
	,_FREQUENCY_NORMAL(1, "发送正常")
	
	;
	private Integer code;

	private String message;

	SmsCode(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
