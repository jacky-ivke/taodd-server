package com.esports.center.basic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * 会员个人信息资料
 *
 * @author jacky
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MemberExtProfileDto implements Serializable {

    private MemberProfileDto userBaseInfo;

    private UnReadMsgDto unreadMsgAmounts;
}
