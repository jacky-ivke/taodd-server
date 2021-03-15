package com.esports.external.handler;

import org.springframework.util.StringUtils;

public enum ProxyCode {

    _YABO_LIVE("YABOZR", "com.esports.external.handler.yabo.live.YaboLiveProxy"),

    _YABO_SLOTS("YABODY", "com.esports.external.handler.yabo.slots.YaboSlotsProxy"),

    _YABO_QP("YABOQP", "com.esports.external.handler.yabo.poker.YaboPokerProxy"),

    _YABO_CP("YABOCP", "com.esports.external.handler.yabo.lottery.YaboLotteryProxy"),

    _XT_AG("AG", "com.esports.external.handler.xint.ag.XintAgProxy"),

    _XT_BG("BG", "com.esports.external.handler.xint.bg.XintBgProxy"),

    _XT_KY("KY", "com.esports.external.handler.xint.ky.XintKyProxy"),

    _XT_LEG("LEG", "com.esports.external.handler.xint.leg.XintLegProxy"),

    _XT_AVIA("AVIA", "com.esports.external.handler.xint.avia.XintAviaProxy"),

    _XT_PT("PT", "com.esports.external.handler.xint.pt.XintPtProxy"),

    _XT_IBC("IBC", "com.esports.external.handler.xint.ibc.XintIbcProxy"),

    _XT_MG("MG", "com.esports.external.handler.xint.mg.XintMgProxy"),

    _XT_BBIN("BBIN", "com.esports.external.handler.xint.bbin.XintBbinProxy"),

    _XT_IA("IA", "com.esports.external.handler.xint.ia.XintIaProxy"),

    _XT_TFG("TFG", "com.esports.external.handler.xint.tfg.XintTfgProxy"),

    _XT_CQ9("CQ9", "com.esports.external.handler.xint.cq9.XintCQ9Proxy"),
    ;

    private String code;

    private String handler;

    ProxyCode(String code, String handler) {
        this.code = code;
        this.handler = handler;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public static String getHandler(String code) {
        String handler = "";
        if (StringUtils.isEmpty(code)) {
            return handler;
        }
        for (ProxyCode proxy : ProxyCode.values()) {
            if (proxy.getCode().equalsIgnoreCase(code)) {
                return proxy.getHandler();
            }
        }
        return handler;
    }

}
