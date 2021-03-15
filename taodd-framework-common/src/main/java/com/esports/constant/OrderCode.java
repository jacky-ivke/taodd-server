package com.esports.constant;

/**
 * 订单状态
 * @author jacky
 *
 */
public enum OrderCode {
	_PENDING(0, "处理中")
	,_SUCCESS(1, "成功")
	,_FAILED(2, "失败")
	,_INVALID(99, "失效")
	;
	private Integer code;

	private String message;

	OrderCode(Integer code, String message) {
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
