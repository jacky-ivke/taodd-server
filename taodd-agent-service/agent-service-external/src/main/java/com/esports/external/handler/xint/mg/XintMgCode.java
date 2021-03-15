package com.esports.external.handler.xint.mg;

import com.esports.constant.BetResultCode;
import com.esports.constant.OrderCode;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

public enum XintMgCode {
	
	_DEFAULT_PWD("XintMg@123","默认密码"),

	_SUCCESS("0", "成功"),
	
	_EXCEPTION("-1","异常"),
	
	_PARAM_ERROR("400","参数错误"),
	
	_ACCOUNT_EXISTS("409","账号重复"),
	
	_ACCOUNT_NOT_EXISTS("-9","账号不存在"),
	
	_BALANCE_NOT_ENOUGH("402", "商戶余额不足"),
	
	_ORDER_FAILURE("order-error", "订单错误")
	;
	private String code;
	
	private String msg;

	XintMgCode(String code, String msg){
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
		for (XintMgCode code : XintMgCode.values()) {
			if (code.getCode().equalsIgnoreCase(errorCode)) {
				return code.getMsg();
			}
		}
		return msg;
	}
	
	 public static Integer getOkStatus(String category) {
    	if("WAGER".equalsIgnoreCase(category)) {
    		return OrderCode._PENDING.getCode();
    	}
    	if("PAYOUT".equalsIgnoreCase(category) || "ENDROUND".equalsIgnoreCase(category)) {
    		return OrderCode._SUCCESS.getCode();
    	}
    	if("REFUND".equalsIgnoreCase(category)) {
    		return OrderCode._FAILED.getCode();
    	}
    	return OrderCode._INVALID.getCode();
    }
	 
	 public static String getResult(String category, BigDecimal profitAmount) {
    	if("WAGER".equalsIgnoreCase(category)) {
    		return BetResultCode._WAIT.getCode();
    	}
    	if("ENDROUND".equalsIgnoreCase(category) && BigDecimal.ZERO.compareTo(profitAmount)<0) {
    		return BetResultCode._WIN.getCode();
    	}
    	if("ENDROUND".equalsIgnoreCase(category) && BigDecimal.ZERO.compareTo(profitAmount)>0) {
    		return BetResultCode._LOSS.getCode();
    	}
    	if("ENDROUND".equalsIgnoreCase(category) && BigDecimal.ZERO.compareTo(profitAmount)==0) {
    		return BetResultCode._DRAW.getCode();
    	}
    	if("REFUND".equalsIgnoreCase(category)) {
    		return BetResultCode._CANCELLED.getCode();
    	}
    	return "";
    }
}
