package com.esports.consumber.service;

import com.esports.constant.GlobalCode;
import com.esports.consumber.bean.db1.CustomerExecutive;
import com.esports.consumber.dao.db1.CustomerExecutiveDao;
import com.esports.consumber.dto.ConfigInfoDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Service
public class CustomerExecutiveManager {

    @Autowired
    private CustomerExecutiveDao customerExecutiveDao;

    public List<ConfigInfoDto> getCustomers(String type) {
        Sort.Order[] orders = {Sort.Order.asc("priority"), Sort.Order.desc("createTime")};
        Sort sort = Sort.by(Arrays.asList(orders));
        Specification<CustomerExecutive> spec = new Specification<CustomerExecutive>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<CustomerExecutive> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 增加筛选条件
                Predicate predicate = cb.conjunction();
                predicate.getExpressions().add(cb.equal(root.get("okStatus").as(Integer.class), GlobalCode._ENABLE.getCode()));
                if (!StringUtils.isEmpty(type)) {
                    predicate.getExpressions().add(cb.equal(root.get("type").as(String.class), type));
                }
                return predicate;
            }
        };
        List<CustomerExecutive> list = customerExecutiveDao.findAll(spec, sort);
        List<ConfigInfoDto> dtos = this.assembleData(list);
        return dtos;
    }

    private List<ConfigInfoDto> assembleData(List<CustomerExecutive> list) {
        List<ConfigInfoDto> dtos = new ArrayList<ConfigInfoDto>();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        Iterator<CustomerExecutive> it = list.iterator();
        ConfigInfoDto dto = null;
        while (it.hasNext()) {
            CustomerExecutive info = it.next();
            dto = new ConfigInfoDto();
            BeanUtils.copyProperties(info, dto);
            dtos.add(dto);
        }
        return dtos;
    }
}
