package com.esports.external.handler.xint.pt;

import org.springframework.util.StringUtils;

public enum XintPtCode {
	
	_DEFAULT_PWD("XintPt123","默认密码"),

	_SUCCESS("0", "成功"),
	
	_EXCEPTION("-1","异常"),
	
	_PARAM_ERROR("999", "参数错误"),
	
	_ACCOUNT_NOT_SPECIFIED("10", "未指定用户名playername"),
	
	_CASINO_NOT_SPECIFIED("11","未指定厂厅名称"),
		
	_ADMIN_ENTITY_NOT_MATCH("12", "管理账号和实体对象不匹配"),
	
	_ADMIN_NOT_EXISTS("14","管理账号必须存在"),
	
	_ENTITY_NOT_EXISTS("15", "实体名称不存在"),
	
	_ACCOUNT_EXISTS("19", "用户账号已经被使用"),
	
	_NICKNAME_IS_NOT_SET("22", "昵称未配置"),
	
	_CASINO_NAME_ERROR("23","厂厅名称格式错误"),
	
	_NICKNAME_USED("24","昵称已被使用"),
	
	_NOT_CHANGEABLE("25", "玩家在线，当前昵称不允许更改"),
	
	_PLAYER_OFFLINE("26","玩家离线"),
	
	_INF_ERROR("27", "技术错误"),
	
	_DATE_FORMAT_ERROR("28", "出生日期格式错误"),
	
	_3RDPCONTAINER_ERROR("29", "3RDPContainer格式错误"),
	
	_ACCOUNT_FORMAT_ERROR("30", "账号格式错误"),
	
	_ACCOUNT_NOT_EXISTS("41", "账号不存在"),
	
	_ACCOUNT_PREFIX_ERROR("42","账号格式错误，前缀无效"),
	
	_LANGUAGE_CODE_ERROR("45", "玩家语言代码受TLE限制"),
	
	_ACTION_NOT_ALLOWED("47", "请求不被允许"),
	
	_PASSWORD_LENGTH_SHORT("50", "账号密码太短"),
	
	_PASSWORD_LENGTH_LONG("51", "账号密码太长"),
	
	_COULD_NOT_LOAD_PLAYER_DATA("71", "无法加载用户数据"),
	
	_CREATE_API_ERROR("72","无法创建用户,API接口错误"),
	
	_DB_ERROR("73", "数据库错误"),
	
	_NAME_ATTR_NOT_EXISTS("75","name不存在"),
	
	_WRONG_REQUEST_FORMAT("76", "错误的请求格式"),
	
	_ADMIN_FROZEN("108", "admin被冻结"),
	
	_BUSINESS_ENTITY_NOT_FOUND("114", "找不到business entity"),
	
	_ENTITY_NOT_FOUND("130", "找不到entity"),
	
	_ADMIN_NOT_FOUND("133", "找不到admin"),
	
	_ADMIN_ACCOUNT_NOT_EXISTS("133","adminname不存在"),
	
	_FORM_VERIFY_ERROR("140", "表单验证错误"),
	
	_ORDER_FAILURE("order-error", "订单错误"),
	
	;
	
	private String code;
	
	private String msg;
	
	XintPtCode(String code, String msg){
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
		for (XintPtCode code : XintPtCode.values()) {
			if (code.getCode().equalsIgnoreCase(errorCode)) {
				return code.getMsg();
			}
		}
		return msg;
	}
	
	public enum OrderStatus{
		FAILD("missing","失败"),
    	SUCCESS("approved","成功"),
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
