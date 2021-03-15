package com.esports.external.handler.xint.ia;

import com.esports.constant.BetResultCode;
import com.esports.constant.OrderCode;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

public enum XintIaCode {
	
	_DEFAULT_PWD("XintIa@123","默认密码"),

	_SUCCESS("0", "成功"),
	
	_EXCEPTION("-1","异常"),
	
	_PARAM_ERROR("9475","参数错误"),
	
	_ACCOUNT_NOT_EXISTS("3004", "用户不存在"),
	
	_ACCOUNT_EXISTS("9411", "玩家已存在"),
	
	_DATA_NOT_EXISTS("9402", "查询数据不存在"),
	
	_SYSTEM_OUT_OF_SERVICE("9400", "系统维护中"),
	
	_ORDER_FAILURE("order-faild","订单错误"),
	
	;
	
	private String code;

	private String msg;

	XintIaCode(String code, String msg){
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
		for (XintIaCode code : XintIaCode.values()) {
			if (code.getCode().equalsIgnoreCase(errorCode)) {
				return code.getMsg();
			}
		}
		return msg;
	}
	
    public enum OrderStatus{
    	STAKE("Stake", "下注"),
    	PAYOFF("Payoff", "派彩"),
    	CANCELSTAKE("cancelStake", "取消本注"),
    	CANCELPAYOFF("cancelPayoff", "取消派彩");
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
		
		public static String getMessage(String errorCode) {
			String msg = errorCode;
			if (StringUtils.isEmpty(errorCode)) {
				return msg;
			}
			for (OrderStatus code : OrderStatus.values()) {
				if (code.getCode().equalsIgnoreCase(errorCode)) {
					return code.getMsg();
				}
			}
			return msg;
		}
	}
    
    public static Integer getOkStatus(String tansType) {
    	if("Stake".equalsIgnoreCase(tansType)) {
    		return OrderCode._PENDING.getCode();
    	}
    	if("Payoff".equalsIgnoreCase(tansType)) {
    		return OrderCode._SUCCESS.getCode();
    	}
    	if("cancelStake".equalsIgnoreCase(tansType) || "cancelPayoff".equalsIgnoreCase(tansType)) {
    		return OrderCode._FAILED.getCode();
    	}
    	return OrderCode._INVALID.getCode();
    }
    
    public static String getResult(String tansType, BigDecimal profitAmount) {
    	if("Stake".equalsIgnoreCase(tansType)) {
    		return BetResultCode._WAIT.getCode();
    	}
    	if("Payoff".equalsIgnoreCase(tansType) && BigDecimal.ZERO.compareTo(profitAmount)<0) {
    		return BetResultCode._WIN.getCode();
    	}
    	if("Payoff".equalsIgnoreCase(tansType) && BigDecimal.ZERO.compareTo(profitAmount)>0) {
    		return BetResultCode._LOSS.getCode();
    	}
    	if("cancelStake".equalsIgnoreCase(tansType) || "cancelPayoff".equalsIgnoreCase(tansType)) {
    		return BetResultCode._CANCELLED.getCode();
    	}
    	return "";
    }
}
