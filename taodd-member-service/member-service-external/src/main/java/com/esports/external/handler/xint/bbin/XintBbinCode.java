package com.esports.external.handler.xint.bbin;

import com.esports.constant.BetResultCode;
import com.esports.constant.OrderCode;
import org.springframework.util.StringUtils;

public enum XintBbinCode {

	_SUCCESS("50001","成功"),
	
	_EXCEPTION("-1","异常"),
	
	_PARAM_ERROR("50002","参数错误"),
	
	_TRANSFER_SUCCESS("11100", "转账成功"),
	
	_TRANSFER_AMOUNT_MUST_INTEGER("10008", "汇款金额不能小于等于 0"),
	
	_KEY_ERROR("44000", "key 验证错误"),
	
	_ORDER_FAILURE("order-error", "订单错误"),
	
	;
	
	private String code;
	
	private String msg;
	
	XintBbinCode(String code, String msg){
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
		for (XintBbinCode code : XintBbinCode.values()) {
			if (code.getCode().equalsIgnoreCase(errorCode)) {
				return code.getMsg();
			}
		}
		return msg;
	}
	
	public enum OrderStatus{
		PENDING("-2", "处理中"),
    	FAILD("-1","失败"),
    	SUCCESS("1","成功"),
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
	
	public static Integer getOkStatus(String status) {
		if("X".equalsIgnoreCase(status)) {
			return OrderCode._PENDING.getCode();
		}
		if("W".equalsIgnoreCase(status) || "L".equalsIgnoreCase(status) || "D".equalsIgnoreCase(status)) {
			return OrderCode._SUCCESS.getCode();
		}
		if("C".equalsIgnoreCase(status)) {
			return OrderCode._FAILED.getCode();
		}
		return OrderCode._INVALID.getCode();
	}
	
	public static String getResult(String status) {
		if("X".equalsIgnoreCase(status)) {
			return BetResultCode._WAIT.getCode();
		}
		if("W".equalsIgnoreCase(status)) {
			return BetResultCode._WIN.getCode();
		}
		if("L".equalsIgnoreCase(status)) {
			return BetResultCode._LOSS.getCode();
		}
		if("D".equalsIgnoreCase(status)) {
			return BetResultCode._DRAW.getCode();
		}
		if("C".equalsIgnoreCase(status)) {
			return BetResultCode._CANCELLED.getCode();
		}
		return "";
	}
	
	public static Integer getSportsOkStatus(String status) {
		if("0".equals(status)) {
			return OrderCode._PENDING.getCode();
		}
		if("2".equals(status) || "3".equals(status) || "4".equals(status)) {
			return OrderCode._SUCCESS.getCode();
		}
		if("5".equals(status) || "-1".equals(status)) {
			return OrderCode._FAILED.getCode();
		}
		return OrderCode._INVALID.getCode();
	}
	
	public static String getSportsResult(String status) {
		if("0".equals(status)) {
			return BetResultCode._WAIT.getCode();
		}
		if("4".equals(status)) {
			return BetResultCode._WIN.getCode();
		}
		if("3".equals(status)) {
			return BetResultCode._LOSS.getCode();
		}
		if("2".equals(status)) {
			return BetResultCode._DRAW.getCode();
		}
		if("5".equals(status) || "-1".equals(status)) {
			return BetResultCode._CANCELLED.getCode();
		}
		return "";
	}
}
