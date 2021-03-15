package com.esports.constant;

/**
 * 下注状态
 * @author jacky
 *
 */
public enum BetResultCode {
	_WAIT("wait","待开奖")
	,_WIN("win", "赢")
	,_LOSS ("loss", "输")
	,_DRAW("draw", "和")
	,_CANCELLED("cancelled", "取消")
	;
	private String code;

	private String message;

	BetResultCode(String code, String message) {
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
