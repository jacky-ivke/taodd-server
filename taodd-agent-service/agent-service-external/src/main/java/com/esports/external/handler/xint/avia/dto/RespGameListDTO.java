package com.esports.external.handler.xint.avia.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RespGameListDTO {
	
	/**
	 * 类型：单关、串关、小游戏
	 */
	@JsonProperty("Type")
	private String type;
	
	/**
	 * 下注流水号
	 */
	@JsonProperty("OrderID")
	private String transactionId;
	
	/**
	 * 玩家游戏账号
	 */
	@JsonProperty("UserName")
	private String gameAccount;
	
	/**
	 * 小游戏类型代码，示例：LOL
	 */
	@JsonProperty("Code")
	private String gameCode;
	
	/**
	 * 游戏期号
	 */
	@JsonProperty("Index")
	private String serialNo;
	
	/**
	 * 玩法
	 */
	@JsonProperty("Player")
	private String gameMode;
	
	/**
	 * 游戏code
	 */
	@JsonProperty("CateID")
	private String gameId;

	/**
	 * 游戏名称
	 */
	@JsonProperty("Category")
	private String gameName;
	
	/**
	 * 游戏类型
	 */
	private String gameType;
	
	/**
	 * 联赛ID
	 */
	@JsonProperty("LeagueID")
	private Integer leagueId;
	
	/**
	 * 联赛名称
	 */
	@JsonProperty("League")
	private String leagueName;
	
	/**
	 * 比赛ID
	 */
	@JsonProperty("MatchID")
	private Integer matchId;
	
	/**
	 * 比赛标题
	 */
	@JsonProperty("Match")
	private String matchName;
	
	/**
	 * 盘口ID
	 */
	@JsonProperty("BetID")
	private Integer betId; 
	
	/**
	 * 盘口名称
	 */
	@JsonProperty("Bet")
	private String betName;
	
	/**
	 * 投注内容
	 */
	@JsonProperty("Content")
	private String Content;
	
	/**
	 * 投注结果
	 */
	@JsonProperty("Result")
	private String result;
	
	/**
	 * 总下注
	 */
	@JsonProperty("BetAmount")
	private BigDecimal betTotal;

	/**
	 * 投注盈亏
	 */
	@JsonProperty("Money")
	private BigDecimal profitAmount;
	
	/**
	 * 有效下注
	 */
	@JsonProperty("BetMoney")
	private BigDecimal betAmount;

	/**
	 * 订单状态
	 */
	@JsonProperty("Status")
	private String okStatus;
	
	/**
	 * 比赛时间
	 */
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss",timezone = "GMT+8")
	@JsonProperty("StartAt")
	private Timestamp gameStartTime;
	
	/**
	 * 比赛时间
	 */
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss",timezone = "GMT+8")
	@JsonProperty("EndAt")
	private Timestamp gameEndTime;
	
	/**
	 * 结算时间
	 */
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss",timezone = "GMT+8")
	@JsonProperty("ResultAt")
	private Timestamp approvalTime;

	/**
	 * 记录创建时间
	 */
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss",timezone = "GMT+8")
	@JsonProperty("CreateAt")
	private Timestamp createTime;
	
	/**
	 * 记录更新时间
	 */
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss",timezone = "GMT+8")
	@JsonProperty("UpdateAt")
	private Timestamp updatedTime;
	
	/**
	 * 派奖时间
	 */
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss",timezone = "GMT+8")
	@JsonProperty("RewardAt")
	private Timestamp rewardTime;
	
	/**
	 * 赔率类型，EU：欧洲赔率，HK：香港赔率
	 */
	@JsonProperty("OddsType")
	private String oddsType;
	
	/**
	 * 赔率
	 */
	@JsonProperty("Odds")
	private BigDecimal odds;
	
	/**
	 * 下注的IP
	 */
	@JsonProperty("IP")
	private String betIp;
	
	/**
	 * 串关详情
	 */
	@JsonProperty("Details")
	private String betDetail;
	
	/**
	 * 会员语言环境
	 */
	@JsonProperty("Language")
	private String language;
	
	/**
	 *会员下注所使用的设备 示例：["PC","Windows"]
	 */
	@JsonProperty("Platform")
	private String[] platform;
	
	/**
	 * 是否有重新结算，1：是 0：否
	 */
	@JsonProperty("ReSettlement")
    private Boolean reSettlement;
	
	/**
	 * 是否是试玩订单 ，1：是 ，0：否
	 */
	@JsonProperty("IsTest")
    private Boolean isTest;
	
	/**
	 * 时间戳，用于时间戳查询
	 */
	@JsonProperty("Timestamp")
	private Long searchTimestamp;
	
	/**
	 * 币种
	 */
	@JsonProperty("Currency")
	private String currency;
}
