package com.esports.constant;

public enum MessageCode {
	_GROUP(1, "群发信息")
	
	,_AGENT(2, "指定代理")
	
	,_MEMBER(3, "指定会员")
	
	,_VIP(4, "指定VIP等级")
	
	,_GRADE(5, "指定层级")
	
	,_EVENT(6, "事件通知")
	
	,_NOTICE(7, "公告")
	
	,_LOGIN(10, "登录通知")
	
	,_STATUS_UNREAD(10, "未读")
	
	,_STATUS_READ(20, "已读")
	
	,_STATUS_DELETE(99, "删除")
	
	,_MANUAL_DEPOSIT(0, "manual-deposit-tips")
	
	,_MANUAL_DRAW(0, "manual-draw-tips")
	
	,_DEPOSIT(0, "deposit-tips")
	
	,_DRAW_SUCCESS(0, "draw-success-tips")
	
	,_DRAW_FAILD(0, "draw-faild-tips")

	,_DRAW_CENTER(0,"agent-draw-center")
	
	,_AGENT_TRANSFER(0,"agent-transfer-tips")
	
	,_AWARD(40, "奖励通知")
	;
	private Integer code;

	private String message;

	MessageCode(Integer code, String message) {
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
