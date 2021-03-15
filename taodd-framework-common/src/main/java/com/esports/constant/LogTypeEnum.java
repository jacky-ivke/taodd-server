package com.esports.constant;

/**
 * 操作变更日志类型
 * @author jacky
 *
 */
public enum LogTypeEnum {

    _C_ACCOUNT("cAccount","【新增账号】 操作人员：%s"),
	_C_AGENT_APPLY("cAccount","【合营申请】 操作人员：%s"),
    _U_ACCOUNT("uAccount","【修改账号】 操作人员：%s"),
    _U_REALNAME("uName","【更新名字】 操作人员：%s"),
    _U_EMAIL("uEamil","【修改邮箱】 操作人员：%s"),
    _U_GRADE("uGrade","【调整层级】 操作人员：%s"),
    _U_AGENT("uAgent","【调整代理】 操作人员：%s"),
    _U_SCHEME("uScheme","【调整方案】 操作人员：%s"),
    _U_TAG("uTag","【风控标签】 操作人员：%s"),
    _U_DRAW_POWER("uDrawPower","【提款权限】 操作人员：%s"),
    _U_LOGIN_PWD("uLoginPwd","【登录密码】 操作人员：%s"),
    _U_TRADE_PWD("uTradePwd","【交易密码】 操作人员：%s"),
    _U_STATUS("uStatus","【账号状态】 操作人员：%s"),
    _U_MOBILE("uMobile","【更新手机】 操作人员：%s"),
	_U_QQ("uqq","【更新QQ】 操作人员：%s"),
	_U_WECHAT("uWechat","【更新微信】 操作人员：%s"),
    _D_BANKCARD("dBankCard","【解绑银行卡】 操作人员：%s"),
    _C_BANKCARD("cBankCard","【绑定银行卡】 操作人员：%s"),
    _U_VIP("uVip","【VIP升级】 操作人员：%s"),
    _U_LOGIN_LIMIT("uLoginLimit","【登录安全限制】 操作人员：%s"),
    _U_CANCEL_LIMIT("uCancelLimit","【解除登录限制】 操作人员：%s"),
    _C_LOGIN_ERROR("cLoginError","【登录密码错误】 操作人员：%s"),
    _U_DOMAIN("uDomain","【修改域名】 操作人员：%s"),
    _U_REMARKS("uRemarks","【修改备注】 操作人员：%s"),
	;
	
	private String type;
	
    private String desc;
	
    LogTypeEnum(String type, String desc){
		this.type = type;
		this.desc = desc;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public static String getRemarks(String type) {
		String remarks = "";
		if(null == type){
			return remarks;
		}
		for(LogTypeEnum enumType :LogTypeEnum.values()){
			 if(enumType.getType().equals(type)){
				 return enumType.getDesc();
			 }
		 }
		 return remarks;
	}
}
