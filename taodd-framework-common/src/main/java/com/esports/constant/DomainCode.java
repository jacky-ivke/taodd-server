package com.esports.constant;

/**
 * 域名类型
 * @author jacky
 *
 */
public enum DomainCode {
	_MAIN("0", "站点域名")
	,_PAY("1", "支付域名")
	,_EXTEND("2", "推广域名")
	;
	private String code;

	private String message;

	DomainCode(String code, String message) {
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

    public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
