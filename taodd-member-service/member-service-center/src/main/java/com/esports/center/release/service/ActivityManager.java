package com.esports.center.release.service;


import com.esports.center.release.bean.db1.Activity;
import com.esports.center.release.dao.db1.ActivityDao;
import com.esports.center.release.dto.ActivityDto;
import com.esports.constant.GlobalCode;
import com.esports.utils.PageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class ActivityManager {

    private final static String LONG_ACTIVITY = "长期优惠";

    private final static String LONG_ATTR = "longdiscount";

    @Autowired
    private ActivityDao activityDao;

    public PageData getActivitys(String type, String attr, Integer source, Integer page, Integer pageSize) {
        Sort.Order[] orders = {Sort.Order.asc("priority"), Sort.Order.asc("id")};
        Sort sort = Sort.by(Arrays.asList(orders));
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Specification<Activity> spec = new Specification<Activity>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Activity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 增加筛选条件
                Predicate predicate = cb.conjunction();
                predicate.getExpressions().add(cb.equal(root.get("okStatus").as(Integer.class), GlobalCode._ENABLE.getCode()));
                if (!StringUtils.isEmpty(type)) {
                    predicate.getExpressions().add(cb.equal(root.get("type").as(String.class), type));
                }
                if (!StringUtils.isEmpty(type)) {
                    predicate.getExpressions().add(cb.equal(root.get("attr").as(String.class), attr));
                }
                if (null != source) {
                    predicate.getExpressions().add(cb.like(root.get("platform").as(String.class), "%" + source + "%"));
                }
                return predicate;
            }
        };
        Page<Activity> pages = activityDao.findAll(spec, pageable);
        List<ActivityDto> list = this.assembleData(pages, source);
        PageData pageData = PageData.builder(pages);
        pageData.setContents(list);
        return pageData;
    }

    private List<ActivityDto> assembleData(Page<Activity> pages, Integer source) {
        List<ActivityDto> dtos = new ArrayList<ActivityDto>();
        List<Activity> list = pages.getContent();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        Iterator<Activity> it = list.iterator();
        ActivityDto dto = null;
        while (it.hasNext()) {
            Activity activity = it.next();
            dto = new ActivityDto();
            String icon = GlobalCode._PC.getCode().equals(source) ? activity.getPcUrl() : activity.getH5Url();
            String timeRange = this.getTimeRange(activity.getAttr(), activity.getStartTime(), activity.getEndTime());
            dto.setName(activity.getTitle());
            dto.setIcon(icon);
            dto.setTimeRange(timeRange);
            dto.setLinkUrl(activity.getDescription());
            dtos.add(dto);
        }
        return dtos;
    }

    private String getTimeRange(String attr, Timestamp startTime, Timestamp endTime) {
        if (LONG_ATTR.equalsIgnoreCase(attr)) {
            return LONG_ACTIVITY;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String st = null != startTime ? df.format(startTime) : "";
        String et = null != endTime ? df.format(endTime) : "";
        String timeRange = st + " - " + et;
        return timeRange;
    }

    public List<Map<String, Object>> getActivityTypes() {
        List<Map<String, Object>> list = Collections.emptyList();
        try {
            list = activityDao.getActivityTypes();
        } catch (Exception e) {
            list = Collections.emptyList();
            log.error("【获取活动类型列表】异常信息：{}", e.getMessage());
        }
        return list;
    }

    public List<Map<String, Object>> getActivityAttrs() {
        List<Map<String, Object>> list = Collections.emptyList();
        try {
            list = activityDao.getActivityAttrs();
        } catch (Exception e) {
            list = Collections.emptyList();
            log.error("【获取活动类型列表】异常信息：{}", e.getMessage());
        }
        return list;
    }

}
