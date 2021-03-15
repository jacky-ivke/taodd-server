package com.esports.external.handler;

import org.springframework.util.StringUtils;

/**
 * 工厂，便于后期扩展其它代理平台的数据数据
 *
 * @author jacky
 */
public class ProxyFactory {
    public static AbstractTemplate getProxy(String merchant) {
        AbstractTemplate proxyFacoty = null;
        if (StringUtils.isEmpty(merchant)) {
            return proxyFacoty;
        }
        try {
            String handler = ProxyCode.getHandler(merchant);
            proxyFacoty = (AbstractTemplate) Class.forName(handler).newInstance();
        } catch (Exception ex) {
            proxyFacoty = null;
            ex.printStackTrace();
        }
        return proxyFacoty;
    }
}
