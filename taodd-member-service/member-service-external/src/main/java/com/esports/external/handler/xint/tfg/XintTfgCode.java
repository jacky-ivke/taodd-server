package com.esports.external.handler.xint.tfg;

import com.esports.constant.BetResultCode;
import com.esports.constant.OrderCode;
import org.springframework.util.StringUtils;

public enum XintTfgCode {

	_SUCCESS("0","成功"),
	
	_EXCEPTION("-1","异常"),
	
	_PARAM_ERROR("11","参数错误"),
	
	_ACCOUNT_NOT_EXISTS("3","用户不存在"),
	
	_AUTHENTICATION_NOT_PROVIDED("12","未提供身份验证凭据"),
	
	;
	private String code;
	
	private String msg;
	
	XintTfgCode(String code, String msg){
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
	
    public enum OrderStatus{
		_SUCCESS("success", "成功");
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
	public static String getMessage(String errorCode) {
		String msg = errorCode;
		if (StringUtils.isEmpty(errorCode)) {
			return msg;
		}
		for (XintTfgCode code : XintTfgCode.values()) {
			if (code.getCode().equalsIgnoreCase(errorCode)) {
				return code.getMsg();
			}
		}
		return msg;
	}
	
	public static Integer getOkStatus(String settlementStatus) {
		if("confirmed".equalsIgnoreCase(settlementStatus)) {
			return OrderCode._PENDING.getCode();
		}
		if("settled ".equalsIgnoreCase(settlementStatus)) {
			return OrderCode._SUCCESS.getCode();
		}
		if("cancelled ".equalsIgnoreCase(settlementStatus)) {
			return OrderCode._FAILED.getCode();
		}
		return OrderCode._INVALID.getCode();
	}
	
	public static String getResult(String resultStatus) {
		if("WIN".equalsIgnoreCase(resultStatus)) {
			return BetResultCode._WIN.getCode();
		}
		if("LOSS".equalsIgnoreCase(resultStatus)) {
			return BetResultCode._LOSS.getCode();
		}
		if("DRAW".equalsIgnoreCase(resultStatus)) {
			return BetResultCode._DRAW.getCode();
		}
		if("CANCELLED".equalsIgnoreCase(resultStatus)) {
			return BetResultCode._CANCELLED.getCode();
		}
		return BetResultCode._WAIT.getCode();
	}
}
