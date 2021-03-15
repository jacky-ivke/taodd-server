package com.esports.constant;

public enum ManualStateEnum {

	SYS_DEPOSIT(0,"com.esports.common.impl.ManualStateDepositImpl","【加币】 操作者:%s,%s"),
	SYS_DEDUCTION(1,"com.esports.common.impl.ManualStateDeductionImpl","【减币】操作者:%s, %s"),
	SYS_REWARD(2,"com.esports.common.impl.ManualStateRewardImpl","【奖励】操作者:%s, %s"),
	DEPOSIT_ORDER(3,"com.esports.common.impl.DepositOrderStateSuccessImpl","【充值】操作者:%s, %s"),
	DRAW_ORDER(5,"com.esports.common.impl.DrawOrderStateFaildImpl","【提现】操作者:%s, %s"),
	SECKILL(6,"com.esports.common.impl.DrawOrderStateSuccessImpl","【返水】操作者:%s, %s"),
	PROFIT(7,"com.esports.common.impl.DrawOrderStateSuccessImpl","【返水】操作者:%s, %s");
	
	private Integer type;
	
    private String className;
    
    private String remarks;
    
	ManualStateEnum(Integer type, String className, String remarks){
		this.type = type;
		this.className = className;
		this.remarks = remarks;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public static String getValue(Integer type){
		String className = "";
		if(null == type){
			return className;
		}
		for(ManualStateEnum enumType :ManualStateEnum.values()){
			 if(enumType.getType().equals(type)){
				 return enumType.getClassName();
			 }
		 }
		 return className;
	}
}
