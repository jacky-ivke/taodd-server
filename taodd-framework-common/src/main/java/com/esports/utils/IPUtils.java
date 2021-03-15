package com.esports.utils;

import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @ClassName: IPUtils
 * @Description: TODO
 * @Author: jacky
 * @Version: V1.0
 */
public class IPUtils {
    private IPUtils() {
    }

    /**
     * @Title: getIpAddr @Description: 获取用户真实IP地址，不使用request.getRemoteAddr()的原因是: 1、有可能用户使用了代理软件方式避免真实IP地址
     * 2、如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值 @param request @return String @throws
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");

        if ((ip != null) && (ip.length() != 0) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.indexOf(",") != -1) {
                ip = ip.split(",")[0];
            }
        }

        if ((ip == null) || (ip.length() == 0) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if ((ip == null) || (ip.length() == 0) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if ((ip == null) || (ip.length() == 0) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }

        if ((ip == null) || (ip.length() == 0) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if ((ip == null) || (ip.length() == 0) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if ((ip == null) || (ip.length() == 0) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if ((ip == null) || (ip.length() == 0) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();

            if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ip = inet.getHostAddress();
            }
        }

        return ip;
    }

    /**
     * @Title: ipToLong @Description: ip地址转成long型数字,将IP地址转化成整数的方法如下 1、通过String的split方法按.分隔得到4个长度的数组
     * 2、通过左移位操作（<<）给每一段的数字加权，第一段的权为2的24次方，第二段的权为2的16次方，第三段的权为2的8次方，最后一段的权为1 @param strIp @return long @throws
     */
    public static long ipToLong(String strIp) {
        long result = 0L;
        if (StringUtils.isEmpty(strIp)) {
            return result;
        }
        if (StringUtils.isEmpty(strIp)) {
            return result;
        }
        String[] ip = strIp.split("\\.");
        if (!ObjectUtils.isEmpty(ip)) {
            result = (Long.parseLong(ip[0]) << 24) + (Long.parseLong(ip[1]) << 16) + (Long.parseLong(ip[2]) << 8)
                    + Long.parseLong(ip[3]);
        }
        return result;
    }

    /**
     * @Title: longToIp @Description: 将十进制整数形式转换成127.0.0.1形式的ip地址 将整数形式的IP地址转化成字符串的方法如下：  
     * 1、将整数值进行右移位操作（>>>），右移24位，右移时高位补0，得到的数字即为第一段IP。   2、通过与操作符（&）将整数值的高8位设为0，再右移16位，得到的数字即为第二段IP。  
     * 3、通过与操作符吧整数值的高16位设为0，再右移8位，得到的数字即为第三段IP。   4、通过与操作符吧整数值的高24位设为0，得到的数字即为第四段IP @param longIp @return String @throws
     */
    public static String longToIp(Long longIp) {
        if (null == longIp) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        // 直接右移24位
        sb.append((longIp >>> 24));
        sb.append(".");
        // 将高8位置0，然后右移16位
        sb.append(((longIp & 0x00FFFFFF) >>> 16));
        sb.append(".");
        // 将高16位置0，然后右移8位
        sb.append(((longIp & 0x0000FFFF) >>> 8));
        sb.append(".");
        // 将高24位置0
        sb.append((longIp & 0x000000FF));
        return sb.toString();
    }

    public static String getArea(String ip) {
        String area = "";
        if (StringUtils.isEmpty(ip)) {
            return area;
        }
        try {
            String filePath = new ClassPathResource("ip2region.db").getFile().getAbsolutePath();
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("Error: Invalid ip2region.db file");
                return area;
            }
            //查询算法
            int algorithm = DbSearcher.BTREE_ALGORITHM;
            DbConfig config = new DbConfig();
            DbSearcher searcher = new DbSearcher(config, filePath);
            Method method = null;
            switch (algorithm) {
                case DbSearcher.BTREE_ALGORITHM:
                    method = searcher.getClass().getMethod("btreeSearch", String.class);
                    break;
                case DbSearcher.BINARY_ALGORITHM:
                    method = searcher.getClass().getMethod("binarySearch", String.class);
                    break;
                case DbSearcher.MEMORY_ALGORITYM:
                    method = searcher.getClass().getMethod("memorySearch", String.class);
                    break;
            }
            DataBlock dataBlock = null;
            if (!Util.isIpAddress(ip)) {
                System.out.println("Error: Invalid ip address");
            }
            dataBlock = (DataBlock) method.invoke(searcher, ip);
            area = dataBlock.getRegion();

        } catch (Exception ex) {
            ex.printStackTrace();
            area = "";
        }
        return area;
    }

    public static String getCountry(String ip) {
        String country = "";
        if (StringUtils.isEmpty(ip)) {
            return country;
        }
        String area = getArea(ip);
        if (StringUtils.isEmpty(area)) {
            return country;
        }
        String[] str = area.split("\\|");
        if (null == str || str.length != 5) {
            return country;
        }
        country = null != str[0] ? str[0] : country;
        return country;
    }

    public static String getCity(String ip) {
        String city = "";
        if (StringUtils.isEmpty(ip)) {
            return city;
        }
        String area = getArea(ip);
        if (StringUtils.isEmpty(area)) {
            return city;
        }
        String[] str = area.split("\\|");
        if (null == str || str.length != 5) {
            return city;
        }
        city = null != str[3] ? str[3] : city;
        return city;
    }
}
