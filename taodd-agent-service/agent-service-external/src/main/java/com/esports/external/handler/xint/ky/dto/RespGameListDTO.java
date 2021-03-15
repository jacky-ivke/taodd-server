package com.esports.external.handler.xint.ky.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RespGameListDTO {
	
	@JsonProperty("GameID")
	private String[] gameId;
	
	@JsonProperty("Accounts")
	private String[] accounts;
	
	@JsonProperty("serverID")
	private String[] serverId;
	
	@JsonProperty("ChairID")
	private String[] chairId;

	@JsonProperty("TableID")
	private String[] tableId;

	@JsonProperty("KindID")
	private String[] kindId;
	
	@JsonProperty("UserCount")
	private Integer[] userCount;
	
	@JsonProperty("CardValue")
	private String[] cardValue;
	
	@JsonProperty("CellScore")
	private BigDecimal[] betAmount;
	
	@JsonProperty("AllBet")
	private BigDecimal[] betTotal;
	
	@JsonProperty("Profit")
	private BigDecimal[] profit;
	
	@JsonProperty("Revenue")
	private BigDecimal[] revenue;
	
	@JsonProperty("GameStartTime")
	private Timestamp[] gameStartTime;
	
	@JsonProperty("GameEndTime")
	private Timestamp[] gameEndTime;
	
	@JsonProperty("ChannelID")
	private Integer[] channelId;
	
	@JsonProperty("LineCode")
	private String[] lineCode;

}
