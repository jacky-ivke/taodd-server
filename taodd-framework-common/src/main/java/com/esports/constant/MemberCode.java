package com.esports.constant;

public enum MemberCode {

	_MEMBER_NORMAL(1, "用户正常状态")

	,_MEMBER_LOCKED(0, "用户锁定状态")

	,_IDENTITY_FORMAL(1, "正式身份")
	
	,_IDENTITY_TEST(0, "模拟身份")
	
	,_IDENTITY_AGENT(2, "代理身份")
	
	,_DEF_PWD(999,"123456")
	
	;
	private Integer code;

	private String message;

	MemberCode(Integer code, String message) {
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
