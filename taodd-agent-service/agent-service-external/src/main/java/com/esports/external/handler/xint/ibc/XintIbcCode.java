package com.esports.external.handler.xint.ibc;

import com.esports.constant.BetResultCode;
import com.esports.constant.OrderCode;
import org.springframework.util.StringUtils;

/**
 * 由于接口状态码不统一，需根据每个接口定义
 * @author jacky
 *
 */
public enum XintIbcCode {
	Common(ErrorType.Common.class),
	Create(ErrorType.Create.class),
	Balance(ErrorType.Balance.class),
	Login(ErrorType.Login.class),
	Transfer(ErrorType.Transfer.class),
	CheckFundTransfer(ErrorType.CheckFundTransfer.class)
	;
	ErrorType[] errorType;
	
	XintIbcCode(Class<? extends ErrorType> errorType){
		this.errorType = errorType.getEnumConstants();
	}

	public ErrorType[] getErrorType() {
		return errorType;
	}

	public void setErrorType(ErrorType[] errorType) {
		this.errorType = errorType;
	}

	interface ErrorType{
		String getCode();
		String getMsg();
		
		enum Common implements ErrorType{
			_SUCCESS("0", "成功"),
			_EXCEPTION("-1","异常"),
			_INF_ERROR("1", "发生错误"),
			_VENDOR_ID_ERROR("9","厂商标识符失效"),
			_OUT_OF_SERVICE("10","系统维护中"),
			_ORDER_FAILURE("order-error", "订单错误"),
			;
			
			private String code;
			
			private String msg;
			
			Common(String code, String msg){
				this.code = code;
				this.msg = msg;
			}
			
			public void setCode(String code) {
				this.code = code;
			}

			public void setMsg(String msg) {
				this.msg = msg;
			}

			public String getCode() {
				return this.code;
			}

			public String getMsg() {
				return this.msg;
			}
			
			public String getMessage(String errorCode) {
				String msg = errorCode;
				if (StringUtils.isEmpty(errorCode)) {
					return msg;
				}
				for (Common code : ErrorType.Common.values()) {
					if (code.getCode().equalsIgnoreCase(errorCode)) {
						return code.getMsg();
					}
				}
				return msg;
			}
			
		}
		
		enum Create implements ErrorType{
			_CREATE_ACCOUNT_EXISTS("2", "会员名称（username）重复"),
			_CREATE_OPERTORID_ERROR("3", "OperatorId错误"),
			_CREATE_ODD_TYPE_ERROR("4","赔率类型格式错误"),
			_CREATE_CURRENCY_TYPE_ERROR("5","币别格式错误"),
			_CREATE_VENDOR_MEMBER_ID_REPEAT("6","厂商会员识别码vendor_member_id重复"),
			_CREATE_TRANSFER_LIMIT_ERROR("7","最小限制转账金额大于最大限制转账金额"),
			_CREATE_PREFIX_ERROR("8","无效的前缀字符"),
			_CREATE_MIN_TRANSFER_AMOUNT_ERROR("11","最小限制转账金额小于 "),
			_CREATE_PARAM_LENGTH_ERROR("12","长度限制 30(Vendor_Member_ID or UserName)"),
			;
			private String code;
			
			private String msg;
			
			Create(String code, String msg){
				this.code = code;
				this.msg = msg;
			}
			
			public void setCode(String code) {
				this.code = code;
			}

			public void setMsg(String msg) {
				this.msg = msg;
			}

			public String getCode() {
				return this.code;
			}

			public String getMsg() {
				return this.msg;
			}
			
			public String getMessage(String errorCode) {
				String msg = errorCode;
				if (StringUtils.isEmpty(errorCode)) {
					return msg;
				}
				for (Create code : ErrorType.Create.values()) {
					if (code.getCode().equalsIgnoreCase(errorCode)) {
						return code.getMsg();
					}
				}
				return msg;
			}
		}
		
		enum Balance implements ErrorType{
			_BALANCE_ACCOUNT_NOT_EXISTS("2","会员不存在"),
			_BALANCE_ACCOUNT_LOCKED("3","会员被封存"),
			_BALANCE_ACCOUNT_NOT_TRANSFER("6","会员尚未转账过"),
			_BALANCE_GET_NON_SPORTS_MEMBER_BALANCE_ERROR("7","取得非体育用户余额错误"),
			;
			private String code;
			
			private String msg;
			
			Balance(String code, String msg){
				this.code = code;
				this.msg = msg;
			}
			
			public void setCode(String code) {
				this.code = code;
			}

			public void setMsg(String msg) {
				this.msg = msg;
			}

			public String getCode() {
				return this.code;
			}

			public String getMsg() {
				return this.msg;
			}
		}
		
		enum CheckFundTransfer implements ErrorType{
			_TRANSFER_SUCCESS("0", "成功"),
			_TRANSFER_FAILD("1", "执行过程中失败"),
			_TRANSFER_NOT_EXISTS("2","交易纪录不存在"),
			_TRANSFER_PENDING("3", "等待五分钟后再次确认"),
			_TRANSFER_WALLET_ID_ERROR("7","wallet_id 输入错误"),
			_TRANSFER_VENDOR_MEMBER_ID_ERROR("9","厂商标识符失效")
			;
			private String code;
			
			private String msg;
			
			CheckFundTransfer(String code, String msg){
				this.code = code;
				this.msg = msg;
			}
			
			public void setCode(String code) {
				this.code = code;
			}

			public void setMsg(String msg) {
				this.msg = msg;
			}

			public String getCode() {
				return this.code;
			}

			public String getMsg() {
				return this.msg;
			}
		}
		
		enum Transfer implements ErrorType{
			_TRANSER_ACCOUNT_NOT_EXISTS("2", "会员不存在"),
			_TRANSFER_BALANCE_NOT_ENOUGH("3","会员余额不足"),
			_TRANSFER_LESSORTHAN_LIMIT_AMOUNT("4","比最小或最大限制的转账金额还更少或更多"),
			_TRANSFER_ORDERNO_EXISTS("5","重复的 vendor_trans_id"),
			_TRANSFER_CURRENCY_ERROR("6","币别错误"),
			_TRANSFER_PARAM_ERROR("7","传入参数错误"),
			_TRANSFER_SYSTEM_LIMIT("8","玩家盈余限制(玩家赢超过系统可转出有效值)"),
			_TRANSFER_SYSTEM_BUSY("11","系统忙绿中，请稍后再试"),
			_TRANSFER_PREFIX_ERROR("12","无效的前缀字符"),
			_TRANSFER_ACCOUNT_FROZEN("13","会员不能被解封存"),
			_TRANSFER_ACCOUNT_LOCKED("14","会员被锁定"),
			_TRANSFER_SINGLE_NOT_SUPPORT("15","单一钱包不支援转出")
			;
			private String code;
			
			private String msg;
			
			Transfer(String code, String msg){
				this.code = code;
				this.msg = msg;
			}
			
			public void setCode(String code) {
				this.code = code;
			}

			public void setMsg(String msg) {
				this.msg = msg;
			}

			public String getCode() {
				return this.code;
			}

			public String getMsg() {
				return this.msg;
			}
		}
		
		enum Login implements ErrorType{
			_LOGIN_ACCOUNT_NOT_EXISTS("2","会员不存在"),
			;
			private String code;
			
			private String msg;
			
			Login(String code, String msg){
				this.code = code;
				this.msg = msg;
			}
			
			public void setCode(String code) {
				this.code = code;
			}

			public void setMsg(String msg) {
				this.msg = msg;
			}

			public String getCode() {
				return this.code;
			}

			public String getMsg() {
				return this.msg;
			}
		}
	}
	
	public static String getMessage(String errorCode, XintIbcCode xintIbcCode) {
		String msg = errorCode;
		if(StringUtils.isEmpty(errorCode)) {
			return msg;
		}
		for(ErrorType errorType : xintIbcCode.errorType) {
			if(errorType.getCode().equals(errorCode)) {
				msg = errorType.getMsg();
			}
		}
		return msg;
	}
	
	public static Integer getOkstatus(String ticketStatus) {
		if("waiting".equalsIgnoreCase(ticketStatus) || "running".equalsIgnoreCase(ticketStatus)) {
			return OrderCode._PENDING.getCode();
		}
		if("refund".equalsIgnoreCase(ticketStatus) || "reject".equalsIgnoreCase(ticketStatus)) {
			return OrderCode._FAILED.getCode();
		}
		return OrderCode._SUCCESS.getCode();
	}
	
	public static String getResult(String ticketStatus) {
		if("won".equalsIgnoreCase(ticketStatus) || "half won".equalsIgnoreCase(ticketStatus)) {
			return BetResultCode._WIN.getCode();
		}
		if("lose".equalsIgnoreCase(ticketStatus) || "half lose".equalsIgnoreCase(ticketStatus)) {
			return BetResultCode._LOSS.getCode();
		}
		if("draw".equalsIgnoreCase(ticketStatus)) {
			return BetResultCode._DRAW.getCode();
		}
		if("refund".equalsIgnoreCase(ticketStatus) || "reject".equalsIgnoreCase(ticketStatus)) {
			return BetResultCode._CANCELLED.getCode();
		}
		if("waiting".equalsIgnoreCase(ticketStatus) || "running".equalsIgnoreCase(ticketStatus)) {
			return BetResultCode._WAIT.getCode();
		}
		return "";
	}
}
