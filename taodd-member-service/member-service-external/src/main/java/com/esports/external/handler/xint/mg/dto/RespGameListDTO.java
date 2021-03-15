package com.esports.external.handler.xint.mg.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class RespGameListDTO {

	
	private String id;
	
	@JsonProperty("wallet_code")
	private String walletCode;
	
	private String category;
	
	@JsonProperty("balance_type")
	private String balanceType;
	
	/**
	 * 此交易后， 钱包为增值或减值， 增值则为 CREDIT， 减值则为 DEBIT
	 */
	private String type;
	
	private BigDecimal amount;
	
	private BigDecimal revenue;
	
	@JsonProperty("currency_unit")
	private String currency;
	
	@JsonProperty("ip")
	private String betIp;
	
	@JsonProperty("account_ext_ref")
	private String gameAccount;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS",timezone = "UTC+8")
	@JsonProperty("created")
	private Timestamp createTime;
	
	@JsonProperty("meta_data")
	private RespMetaDataDTO metaData;
	
	@JsonIgnore
	public String getRoundId() {
		String roundId = "";
		if(null == this.metaData) {
			return roundId;
		}
		roundId = this.metaData.getRoundId();
		return roundId;
	}
	
	@JsonIgnore
	public String getTransactionId() {
		String id = this.getId()+this.getRoundId();
		return id;
	}
	
	@JsonIgnore
	public String getGameId() {
		String gameId = "";
		if(null == this.metaData) {
			return gameId;
		}
		gameId = this.metaData.getGameId();
		return gameId;
	}
	
	@JsonIgnore
	public String getGameName() {
		String gameName = "";
		if(null == this.metaData) {
			return gameName;
		}
		gameName = this.metaData.getGameName();
		return gameName;
	}	
	
	
}
