package com.esports.constant;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public enum GlobalCode {

    _PC(0, "PC端"), _MOBILE(1, "移动端"), _ENABLE(1, "开启"), _DISABLE(0, "关闭"), _PENDIGN_AUDIT(0, "待审"), _AUDIT_SUCCESS(1, "审核通过"), _AUDIT_FAILED(2, "审核失败"), _GM2_PRIFIX(null, "ZHP"), AGENT_DEFAULT_PWD(null, "888888"), _DICT_GAME_TYPE(null, "dict_game_type"), _DICT_DEPOSIT_CONDITION(null, "dict_depoist_grad"), _IDENTITY_FORMAL(1, "正式玩家"), _IDENTITY_TEST(0, "测试玩家"), _DEFAULT_GRADE(null, "L1"), _DEFAULT_AGENT(null, "agent000"), _DEFAULT_TOP_AGENT(null, "top000"), _DEFAULT_PWD(null, "Asdf@1234"), _CARD_LIMIT_NUM(3, "银行卡限制数量"), _LOGIN_ERROR_LIMIT(5, "30"), _SYS_ACTION(null, "系统通知");

    private Integer code;

    private String message;

    GlobalCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static Integer getSourceTerminal(HttpServletRequest request) {
        String userSource = request.getHeader(CommonCode.USER_SOURCE);
        Integer source = !StringUtils.isEmpty(userSource) ? Integer.valueOf(userSource) : null;
        source = null == source || GlobalCode._PC.getCode().equals(source) ? GlobalCode._PC.getCode() : GlobalCode._MOBILE.getCode();
        return source;
    }

    public static Integer getSource(HttpServletRequest request) {
        String userSource = request.getHeader(CommonCode.USER_SOURCE);
        Integer source = !StringUtils.isEmpty(userSource) ? Integer.valueOf(userSource) : null;
        source = null == source || GlobalCode._PC.getCode().equals(source) ? GlobalCode._PC.getCode() : source;
        return source;
    }
}
