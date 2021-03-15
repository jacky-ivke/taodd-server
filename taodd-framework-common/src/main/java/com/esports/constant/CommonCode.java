package com.esports.constant;

public interface CommonCode {

    /**
     * jwt 基于token的用户账号
     */
    String JWT_USERNAME = "account";

    /**
     * 用户来源
     */
    String USER_SOURCE = "source";

    /**
     * 访问令牌
     */
    String TOKEN_KEY = "token";

    /**
     * 签名字符串
     */
    String SIGN_KEY = "signature";

    /**
     * 缓存的Key默认失效时间3s
     */
    int REDIS_DEFAULT_EXPRISE = 3;

    int REQUEST_API_NUMS = 20;

    /**
     * 接口访问失效时间
     */
    int INF_FAILURE_TIME = 60 * 1000;

    /**
     * 时间戳
     */
    String FAILURE_TIME_KEY = "timestamp";

    /**
     * 在线状态缓存失效时间
     */
    Long ONLINE_EXPRIRED_TIME = 60L;

    /**
     * 接口安全KEY
     */
    String SECURITY_KEY = "appKey";

    /**
     * 接口签名加密串
     */
    String SECURITY_SALT = "aHR0cHM6Ly9teS5vc2NoaW5hLm5ldC91LzM2ODE4Njg=";

    String MOBILE = "mobile";

    String FLASH = "flash";
}
