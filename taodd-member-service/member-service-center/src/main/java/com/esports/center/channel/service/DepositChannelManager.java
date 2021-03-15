package com.esports.center.channel.service;

import com.esports.center.channel.bean.db1.DepositChannel;
import com.esports.center.channel.dao.db1.DepositChannelDao;
import com.esports.center.channel.dto.DepositChannelDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DepositChannelManager {

    @Autowired
    private DepositChannelDao depositChannelDao;

    public List<Map<String, Object>> getChannelTypes() {
        List<Map<String, Object>> list = depositChannelDao.getChannelTypes();
        return list;
    }

    public List<Map<String, Object>> getDepositTypes() {
        List<Map<String, Object>> list = depositChannelDao.getDepositOrderTypes();
        return list;
    }

    public List<DepositChannelDto> getChannels(String account) {
        List<DepositChannelDto> dtos = Collections.emptyList();
        Long gradeId = depositChannelDao.getGradeIdByAccount(account);
        List<DepositChannel> channels = depositChannelDao.getChannels(gradeId);
        dtos = this.assembleData(channels);
        return dtos;
    }

    private List<DepositChannelDto> assembleData(List<DepositChannel> channels) {
        List<DepositChannelDto> dtos = Collections.emptyList();
        if (CollectionUtils.isEmpty(channels)) {
            return dtos;
        }
        dtos = new ArrayList<DepositChannelDto>();
        DepositChannelDto dto = null;
        for (DepositChannel channel : channels) {
            dto = new DepositChannelDto();
            dto.setChannelId(channel.getId());
            dto.setChannelName(channel.getChannelName());
            dto.setPayType(channel.getPayType());
            dto.setDomain(channel.getDomain());
            dto.setIcon(channel.getIcon());
            dto.setMinAmount(null == channel.getMinAmount() ? BigDecimal.ZERO : channel.getMinAmount());
            dto.setMaxAmount(null == channel.getMaxAmount() ? BigDecimal.ZERO : channel.getMaxAmount());
            dtos.add(dto);
        }
        return dtos;
    }

    public boolean checkDepositRule(Long channelId, BigDecimal depositAmount) {
        boolean success = true;
        DepositChannel rule = depositChannelDao.getOne(channelId);
        BigDecimal minAmount = null != rule && null != rule.getMinAmount() ? rule.getMinAmount() : BigDecimal.ZERO;
        BigDecimal maxAmount = null != rule && null != rule.getMaxAmount() ? rule.getMaxAmount() : BigDecimal.ZERO;
        if (minAmount.compareTo(BigDecimal.ZERO) >= 0 && maxAmount.compareTo(minAmount) > 0) {
            //充值渠道金额限制，目前只限制下限,对上限当前不予限制
            if (depositAmount.compareTo(minAmount) < 0) {
                success = false;
            }
        }
        return success;
    }

    public DepositChannel getChannelRule(Long channelId) {
        DepositChannel rule = null;
        if (null == channelId) {
            return rule;
        }
        rule = depositChannelDao.getOne(channelId);
        return rule;
    }
}
