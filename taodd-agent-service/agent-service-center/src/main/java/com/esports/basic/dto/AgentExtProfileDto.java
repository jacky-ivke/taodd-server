package com.esports.basic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * 代理个人信息资料
 *
 * @author jacky
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AgentExtProfileDto implements Serializable {

    private AgentProfileDto userBaseInfo;

    private UnReadMsgDto unreadMsgAmounts;
}
