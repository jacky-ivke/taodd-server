package com.esports.center.channel.service;

import com.esports.center.channel.bean.db1.DrawChannel;
import com.esports.center.channel.dao.db1.DrawChannelDao;
import com.esports.center.channel.dto.DrawChannelDto;
import com.esports.constant.GlobalCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DrawChannelManager {

    @Autowired
    private DrawChannelDao drawChannelDao;

    public List<DrawChannelDto> getChannels() {
        Specification<DrawChannel> spec = new Specification<DrawChannel>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<DrawChannel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                predicate.getExpressions().add(cb.equal(root.get("okStatus").as(Integer.class), GlobalCode._ENABLE.getCode()));
                return predicate;
            }
        };
        List<DrawChannel> list = drawChannelDao.findAll(spec);
        List<DrawChannelDto> dtos = this.assembleData(list);
        return dtos;
    }

    private List<DrawChannelDto> assembleData(List<DrawChannel> list) {
        List<DrawChannelDto> dtos = new ArrayList<DrawChannelDto>();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        Iterator<DrawChannel> it = list.iterator();
        DrawChannelDto dto = null;
        while (it.hasNext()) {
            DrawChannel channel = it.next();
            dto = new DrawChannelDto();
            dto.setChannelName(channel.getChannelName());
            dto.setChannelCode(channel.getChannelCode());
            dto.setIcon(channel.getIcon());
            dto.setSimpleIcon(channel.getLogo());
            List<String> stringList = dtos.stream().map(DrawChannelDto::getChannelCode).collect(Collectors.toList());
            long count = stringList.stream().distinct().count();
            if(count == stringList.size()){
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public DrawChannel findByChannelCode(String channelCode) {
        DrawChannel drawChannel = null;
        if (StringUtils.isEmpty(channelCode)) {
            return drawChannel;
        }
        List<DrawChannel> list = drawChannelDao.findByChannelCode(channelCode);
        if(CollectionUtils.isEmpty(list)){
            return drawChannel;
        }
        drawChannel = list.get(0);
        return drawChannel;
    }
}
