package com.esports.external.handler.xint.ibc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RespGameListDTO {
	
	/**
	 * 下注流水号
	 */
	@JsonProperty("trans_id")
	private String transactionId;
	
	/**
	 * 玩家游戏账号
	 */
	@JsonProperty("vendor_member_id")
	private String gameAccount;
	
	/**
	 * 厂商 ID。此 ID 为厂商自行定义
	 */
	@JsonProperty("operator_id")
	private String operatorId; 
	
	/**
	 * 联賽编号
	 */
	@JsonProperty("league_id")
	private String leagueId;
	
	/**
	 * 联赛名称
	 */
	@JsonProperty("leaguename")
	private RespNameDTO[] leagueNames;
	
	/**
	 * 总下注
	 */
	@JsonProperty("stake")
	private BigDecimal betTotal;

	/**
	 * 盈利金额
	 */
	@JsonProperty("winlost_amount")
	private BigDecimal profitAmount;

	/**
	 * 赛事编号
	 */
	@JsonProperty("match_id")
	private Integer matchId;
	
	/**
	 * 主队编号
	 */
	@JsonProperty("home_id")
	private Integer homeId;
	
	/**
	 * 主队名称
	 */
	@JsonProperty("hometeamname")
	private RespNameDTO[] homeNames;
	
	/**
	 * 客队编号
	 */
	@JsonProperty("away_id")
	private Integer awayId;
	
	/**
	 * 客队名称
	 */
	@JsonProperty("awayteamname")
	private RespNameDTO[] awayNames;
	
	/**
	 * 队伍编号
	 */
	@JsonProperty("team_id")
	private Integer teamId;
	
	/**
	 *赛事开球时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss",timezone = "GMT+8")
	@JsonProperty("match_datetime")
	private Timestamp matchTime;
	
	/**
	 * 体育种类
	 */
	@JsonProperty("sport_type")
	private Integer sportType;
	
	/**
	 * 下注类型
	 */
	@JsonProperty("bet_type")
	private Integer betType;
	
	/**
	 * 注单状态
	 */
	@JsonProperty("ticket_status")
	private String ticketStatus;
	
	/**
	 * 赔率
	 */
	@JsonProperty("odds")
	private BigDecimal odds;
	
	/**
	 * 结算时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss",timezone = "GMT+8")
	@JsonProperty("settlement_time")
	private Timestamp approvalTime;

	/**
	 * 记录创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss",timezone = "GMT+8")
	@JsonProperty("transaction_time")
	private Timestamp createTime;
	
	/**
	 * 版本号
	 */
	@JsonProperty("version_key")
	private Long versionKey;

}
