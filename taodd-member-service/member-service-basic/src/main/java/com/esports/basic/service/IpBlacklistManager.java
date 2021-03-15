package com.esports.basic.service;

import com.esports.basic.bean.db1.IpBlacklist;
import com.esports.basic.dao.db1.IpBlacklistDao;
import com.esports.cache.CacheConfig;
import com.esports.constant.RedisCacheKey;
import com.esports.utils.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class IpBlacklistManager {

    @Autowired
    private IpBlacklistDao ipBlacklistDao;

    public boolean checkIpForbidden(String ip) {
        boolean success = false;
        IpBlacklist ipBlacklist = this.findByIp(ip);
        if (null != ipBlacklist) {
            success = true;
        }
        return success;
    }

    @Cacheable(key = "#root.target.getFormatKey(#p0)", cacheManager = CacheConfig.CacheManagerNames.REDIS_CACHE_MANAGER, cacheNames = CacheConfig.RedisCacheNames.CACHE_60MINS)
    public IpBlacklist findByIp(String ip) {
        IpBlacklist ipBlacklist = null;
        Long ipFrom = IPUtils.ipToLong(ip);
        List<IpBlacklist> list = ipBlacklistDao.findByIp(ipFrom, ipFrom);
        if (!CollectionUtils.isEmpty(list)) {
            ipBlacklist = list.get(0);
        }
        return ipBlacklist;
    }

    public String getFormatKey(String ip) {
        String key = String.format(RedisCacheKey.IP_FORBIDDEN_KEY, ip);
        return key;
    }

}
