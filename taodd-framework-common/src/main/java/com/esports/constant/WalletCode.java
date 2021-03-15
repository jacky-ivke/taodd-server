package com.esports.constant;

/**
 * 域名类型
 * @author jacky
 *
 */
public enum WalletCode {
	_BALANCE("center", "余额钱包")
	,_INTEREST("interest", "利息钱包")
	,_INVITATION("invitation", "邀请佣金")
	,_COMMISSION("commission", "代理佣金")
	,_OTHER("other", "代存钱包")
	;
	private String code;

	private String message;

	WalletCode(String code, String message) {
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
