package com.esports.constant;

/**
 * 资金涉及类型
 * @author jacky
 */
public enum CapitalCode {
	_DEPOSIT("deposit","存款")
		,_DEPOSIT_ONEKAY("onekeyDeposit", "一键转入")
		,_DEPOSIT_MANUAL("manualDeposit", "人工存款")
		,_DEPOSIT_COMPANY("companyDeposit", "公司入款")
		,_DEPOSIT_ONLINE("onlineDeposit", "线上支付")
		,_DEPOSIT_AGENT("agentTransferDeposit", "上级发放")
	,_DRAW("draw", "取款")
		,_DRAW_ONEKAY("onekeyOut", "一键转出")
		,_DRAW_MANUAL("manualOut", "人工取款")
		,_DRAW_ONLINE("onlineOut", "在线取款")
		,_DRAW_CENTER("drawCenterOut", "取款至中心账户")
	,_AWARD("award", "奖励")
	    ,_AWARD_RAKEBACK("rakebackAward", "返水奖励")
	    ,_AWARD_COMMISSION("commissionAward", "返佣奖励")
	    ,_AWARD_INTEREST("interest", "理财收益")
	    ,_AWARD_FIRST_DEPOSIT("firstdeposit", "首存奖励")
	,_INTEREST("interest", "投资理财")
        ,_INTEREST_PLAN("interestPlan", "理财计划")
        ,_INTEREST_PROFIT("interestProfit", "利息钱包")
    ,_INVITATION("invitation","邀请返佣")     
        ,_FIRST_INVITATION("first", "一级返佣")
        ,_SECOND_INVITATION("second", "二级返佣")
        ,_THIRD_INVITATION("third", "三级返佣")
    ,_TRASNFER("transfer", "转账")
        ,_TRANSFER_MEMBER("transferSubMember", "代理代存")
        ,_TRANSFER_OTHER("transferOther", "代理转账")
    ,_AGENT_COMMISSION("agentCommission", "代理返佣")
        ,_COMMISSION_SETTLED("settledCommission", "返佣结算")
        ,_COMMISSION_KEEP("keepCommission", "返佣记账")
        ,_COMMISSION_CKEAR("clearCommission", "返佣平账")
	;
	
	private String code;

	private String message;

	CapitalCode(String code, String message) {
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

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	public static String getRemarks(String code) {
		String remarks = "";
		if(null == code){
			return remarks;
		}
		for(CapitalCode enumType :CapitalCode.values()){
			 if(enumType.getCode().equals(code)){
				 return enumType.getMessage();
			 }
		 }
		 return remarks;
	}
}
