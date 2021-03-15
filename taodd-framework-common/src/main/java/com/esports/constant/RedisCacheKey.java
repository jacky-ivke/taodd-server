package com.esports.constant;

public interface RedisCacheKey {

	/**
	 * 会员银行卡缓存KEY
	 */
    String MEMBER_CARD_KEY = "member.bankcard:%s";
	
	/**
	 * 提现规则缓存KEY
	 */
    String DRAW_RULE_KEY = "config:drawrule";
	
	/**
	 * 理財套餐緩存KEY
	 */
    String WEALTH_RULE_KEY = "config:wealth";
	
	/**
	 * 系统公告缓存KEY
	 */
    String NOTICE_KEY = "general:notice";
	
	/**
	 * 系统活动缓存KEY
	 */
    String ACTIVITY_KEY = "general:activity";
	
	
	/**
	 * 系统轮播图缓存KEY
	 */
    String BANNER_KEY = "general:banner:%s";
	
	/**
	 * 玩家在线KEY
	 */
    String ONLINE_KEY = "online:%s";
	
	/**
	 * 单点登录
	 */
    String MEMBER_SSO_KEY = "member.sso:%s";

	/**
	 * 会员忘记密码，发验证码的手机和提交手机号未校验
	 */
	String SECOND_CHEK_KEY = "member.password:%s";
	   
    /**
     * 单点登录
     */
    String AGENT_SSO_KEY = "agent.sso:%s";
    
    /**
     * 会员系统连续登录错误次数
     */
    String MEMBER_LOGIN_ERROR = "member.login.error:%s";
    
    /**
     * 会员系统连续登录错误次数
     */
    String AGENT_LOGIN_ERROR = "agent.login.error:%s";
    
    /**
     * IP访问频率KEY
     */
    String IP_CHECK_KEY = "ip.visit:%s-%s";
    
    /**
     * 限制IP
     */
    String IP_FORBIDDEN_KEY = "ip.blacklist:%s";
    
}
