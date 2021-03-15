package com.esports.constant;

/**
 * 游戏类型
 * @author jacky
 *
 */
public enum GameTypeCode {
	_LIVE("live", "真人")
	,_SLOTS("slots", "电游")
	,_TABLE_GAME("table_game", "棋牌")
	,_SPORTS("sports", "体育")
	,_FISH_ARCADE("fish_arcade", "捕鱼")
	,_LOTTO("lotto", "彩票")
	;
	private String code;

	private String message;

	GameTypeCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
