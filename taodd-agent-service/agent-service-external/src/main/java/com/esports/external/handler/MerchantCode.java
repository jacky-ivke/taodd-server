package com.esports.external.handler;

public enum MerchantCode {
    _YABO("YABO", "YABO"),
    _XT("XT", "XT"),
    GM2("GM2", "GM2");

    private String code;

    private String message;

    MerchantCode(String code, String message) {
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
