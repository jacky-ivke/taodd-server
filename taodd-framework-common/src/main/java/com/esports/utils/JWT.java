package com.esports.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app.jwt")
@Data
public final class JWT implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(JWT.class);

    @Override
    public void afterPropertiesSet() throws Exception {
//		logger.info("JWT加载信息【有效时间：{}s, 用户凭证：{},加密密钥：{}】", this.getExpire(), this.getHeader(), this.getSecret());
    }

    /**
     * 加密秘钥
     */
    private String secret;
    /**
     * 有效时间,单位(s)
     */
    private Long expire;
    /**
     * 用户凭证
     */
    private String header;

    /**
     * 生成Token签名
     *
     * @param userAccount 用户账号
     * @return 签名字符串
     * @author jacky
     */
    public String generateToken(String userAccount) {
        Date expireDate = new Date(System.currentTimeMillis() + this.getExpire() * 1000);
        return Jwts.builder().setHeaderParam("auth", "JWT") // 设置头部信息
                .setSubject(userAccount).setExpiration(expireDate) // token过期时间
                .signWith(SignatureAlgorithm.HS512, this.getSecret()).compact();
    }

    /**
     * 生成Token签名
     */
    public String generateToken(String userAccount, Long ttlMillis, Map<String, Object> map) {
        ttlMillis = null == ttlMillis ? this.getExpire() : ttlMillis;
        Date expireDate = new Date(System.currentTimeMillis() + ttlMillis * 1000);
        if (CollectionUtils.isEmpty(map)) {
            map = new HashMap<String, Object>();
            map.put("userid", userAccount);
        }
        return Jwts.builder().setHeaderParam("auth", "JWT") // 设置头部信息
                .setClaims(map) // 装入自定义的用户信息
                .setSubject(userAccount).setExpiration(expireDate) // token过期时间
                .signWith(SignatureAlgorithm.HS512, this.getSecret()).compact();
    }

    /**
     * 获取签名信息
     *
     * @param token
     * @author jacky
     */
    public Claims getClaimByToken(String token) {
        Claims claims = null;
        if (!StringUtils.isEmpty(token)) {
            try {
                // 得到DefaultJwtParser
                claims = Jwts.parser()
                        // 设置签名的秘钥
                        .setSigningKey(this.getSecret())
                        // 设置需要解析的jwt
                        .parseClaimsJws(token).getBody();
            } catch (Exception e) {
                claims = null;
            }
        }
        return claims;
    }

    public String getSubject(String token) {
        String result = "";
        try {
            if (StringUtils.isEmpty(token)) {
                return result;
            }
            boolean isexpired = this.isTokenExpired(token);
            if(isexpired){
                return result;
            }
            Claims claims = this.getClaimByToken(token);
            if (null != claims) {
                result = claims.getSubject();
            }
        } catch (Exception ex) {
			result = "";
        }
        return result;
    }

    /**
     * 判断Token是否过期
     *
     * @param token
     * @return true 过期
     * @author jacky
     */
    public boolean isTokenExpired(String token) {
        boolean isexpired = false;
        try {
            Claims claims = this.getClaimByToken(token);
            if (null != claims) {
                Date expiration = claims.getExpiration();
                isexpired = expiration.before(new Date());
            } else {
                isexpired = true;
            }
        } catch (Exception ex) {
            isexpired = true;
        }
        return isexpired;
    }

}
