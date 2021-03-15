package com.esports.carousel.service;


import com.esports.cache.CacheConfig;
import com.esports.carousel.bean.db1.Banner;
import com.esports.carousel.dao.db1.BannerDao;
import com.esports.carousel.dto.BannerDto;
import com.esports.column.dto.NavigationDto;
import com.esports.column.service.NavigationManager;
import com.esports.constant.GlobalCode;
import com.esports.constant.RedisCacheKey;
import com.esports.utils.PageData;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service
public class BannerManager {

    @Autowired
    private BannerDao bannerDao;

    @Autowired
    private NavigationManager navigationManager;

    public String getFormatKey(String navCode) {
        String key = String.format(RedisCacheKey.BANNER_KEY, navCode);
        return key;
    }

    public List<BannerDto> getBannersByNavCode(String navCode, Integer source){
        List<BannerDto> dtos = Collections.emptyList();
        List<Banner> banners = bannerDao.getBannersByNavCode(navCode, source);
        if(CollectionUtils.isEmpty(banners)){
            return dtos;
        }
        dtos = new ArrayList<BannerDto>();
        BannerDto dto = null;
        Iterator<Banner> iterator = banners.iterator();
        while (iterator.hasNext()) {
            Banner banner = iterator.next();
            dto = new BannerDto();
            dto.setTitle(banner.getTitle());
            dto.setUrl(banner.getUrl());
            dto.setLink(banner.getLink());
            dtos.add(dto);
        }
        return dtos;
    }


    public JSONArray getBanners(Integer source){
        JSONArray jsonArray = null;
        //只处理root栏目下的轮播图
        List<NavigationDto> navs = navigationManager.getTopNavs(source);
        if(CollectionUtils.isEmpty(navs)){
            return jsonArray;
        }
        jsonArray = new JSONArray();
        JSONObject json = null;
        for(NavigationDto nav : navs){
            json = new JSONObject();
            json.put(nav.getCode(), this.getBannersByNavCode(nav.getCode(), source));
            jsonArray.add(json);
        }
        return jsonArray;
    }


    @Cacheable(key = "#root.target.getFormatKey(#p0)", cacheManager = CacheConfig.CacheManagerNames.REDIS_CACHE_MANAGER, cacheNames = CacheConfig.RedisCacheNames.CACHE_30MINS)
    public PageData getBanners(String navCode, Integer source, Integer page, Integer pageSize) {
        Sort.Order[] orders = {Sort.Order.asc("prority"), Sort.Order.asc("id")};
        Sort sort = Sort.by(Arrays.asList(orders));
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Specification<Banner> spec = new Specification<Banner>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Banner> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if (!StringUtils.isEmpty(navCode)) {
                    predicate.getExpressions().add(cb.equal(root.get("navCode").as(String.class), navCode));
                }
                if (null != source) {
                    predicate.getExpressions().add(cb.like(root.get("platform").as(String.class), "%" + source + "%"));
                }
                predicate.getExpressions().add(cb.equal(root.get("okStatus").as(Integer.class), GlobalCode._ENABLE.getCode()));
                return predicate;
            }
        };
        Page<Banner> pages = bannerDao.findAll(spec, pageable);
        List<BannerDto> list = this.assembleData(pages);
        PageData pageData = PageData.builder(pages);
        pageData.setContents(list);
        return pageData;
    }

    public List<BannerDto> assembleData(Page<Banner> pages) {
        List<BannerDto> dtos = new ArrayList<BannerDto>();
        List<Banner> list = pages.getContent();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        BannerDto dto = null;
        Iterator<Banner> iterator = list.iterator();
        while (iterator.hasNext()) {
            Banner banner = iterator.next();
            dto = new BannerDto();
            dto.setTitle(banner.getTitle());
            dto.setUrl(banner.getUrl());
            dto.setLink(banner.getLink());
            dtos.add(dto);
        }
        return dtos;
    }
}
