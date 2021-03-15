package com.esports.external.handler.xint.avia;

import org.springframework.util.StringUtils;

public enum XintAviaCode {
	
	_DEFAULT_PWD("XintAvia@123","默认密码"),

	_SUCCESS("1", "成功"),
	
	_EXCEPTION("-1", "异常"),
	
	_PARAM_ERROR("CONTENT","内容错误（提交的参数不符合规则）"),
	
	_ACCOUNT_NOT_EXISTS("NOUSER","用户不存在"),
	
	_ACCOUNT_FORMAT_ERROR("BADNAME","用户名不符合规则,用户名格式为数字+字母+下划线的组合，2~16位"),
	
	_PASSWORD_FORMAT_ERROR("BADPASSWORD","密码不符合规则，密码长度位5~16位"),
	
	_ACCOUNT_EXISTS("EXISTSUSER", "用户名已经存在"),
	
	_AMOUNT_ABNORMAL("BADMONEY","金额错误,金额支持两位小数"),
	
	_ORDERNO_EXISTS("EXISTSORDER","订单号已经存在，转账订单号为全局唯一"),
	
	_ORDERNO_FORMAT_ERROR("NOORDER","订单号错误（不符合规则或者不存在）"),
	
	_TRANSFER_NO_ACTION("TRANSFER_NO_ACTION", "未指定转账动作，转账动作必须为 IN 或者 OUT"),
	
	_IP_DISABLED("IP","IP未授权"),
	
	_ACCOUNT_LOCKED("USERLOCK", "用户被锁定，禁止登录"),
	
	_BALANCE_NOT_ENOUGH("NOBALANCE", "余额不足"),
	
	_MERCHANT_BALANCE_NOT_ENOUGH("NOCREDIT","平台额度不足（适用于买分商户)"),
	
	_SECURITY_KEY_ERROR("Authorization","API密钥错误"),
	
	_INF_ERROR("Faild","发生错误"),
	
	_UNKNOWN_DOMAIN("DOMAIN","未配置域名（请与客服联系）"),
	
	_SIGNATURE_ERROR("Sign","签名错误（适用于单一钱包的通信错误提示）"),
	
	_NO_SUPPORT_ACTION("NOSUPPORT","不支持该操作"),
	
	_REQUEST_TIMEOUT("TIMEOUT", "超时请求"),
	
	_MERCHANTS_LOCKED("STATUS","状态错误(商户被冻结）"),
	
	_MERCHANTS_CONFIG_ERROR("CONFIGERROR", "商户信息配置错误（请联系客服处理）"),
	
	_SEARCH_TIME_ERROR("DATEEROOR","查询日期错误,日期超过了1天或者结束时间大于开始时间"),
	
	_ORDERNO_NOT_EXISTS("ORDER_NOTFOUND","查询使用的订单号不存在"),
	
	_PROCCESSING("PROCCESSING","订单正在处理中"),
	
	_ORDER_FAILURE("order-error", "订单错误"),
	;
	
	private String code;
	
	private String msg;
	
	XintAviaCode(String code, String msg){
		this.code = code;
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public static String getMessage(String errorCode) {
		String msg = errorCode;
		if (StringUtils.isEmpty(errorCode)) {
			return msg;
		}
		for (XintAviaCode code : XintAviaCode.values()) {
			if (code.getCode().equalsIgnoreCase(errorCode)) {
				return code.getMsg();
			}
		}
		return msg;
	}
	
	public enum OrderStatus{
		PENDING("None", "处理中"),
    	FAILD("Faild","失败"),
    	SUCCESS("Finish","成功"),
    	;
		private String code;
		private String msg;
		OrderStatus(String code, String msg){
			this.code = code;
			this.msg = msg;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
	}
}
