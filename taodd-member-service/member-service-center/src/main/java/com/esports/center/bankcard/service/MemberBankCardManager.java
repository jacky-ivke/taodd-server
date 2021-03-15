package com.esports.center.bankcard.service;


import com.esports.api.log.MemberLogService;
import com.esports.cache.CacheConfig;
import com.esports.center.bankcard.bean.db1.MemberBankCard;
import com.esports.center.bankcard.dao.db1.MemberBankCardDao;
import com.esports.center.bankcard.dto.BankCardDto;
import com.esports.center.channel.bean.db1.DrawChannel;
import com.esports.center.channel.service.DrawChannelManager;
import com.esports.constant.GlobalCode;
import com.esports.constant.LogTypeEnum;
import com.esports.constant.RedisCacheKey;
import com.esports.utils.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


@Slf4j
@Service
public class MemberBankCardManager {

    @Autowired
    private MemberBankCardDao bankCardDao;

    @Autowired
    private MemberLogService memberLogService;

    @Autowired
    private DrawChannelManager drawChannelManager;

    public Boolean checkCardNo(String bankAccount) {
        Boolean success = true;
        Integer okStatus = GlobalCode._ENABLE.getCode();
        MemberBankCard card = bankCardDao.findByBankAccountAndOkStatus(bankAccount, okStatus);
        if (null != card) {
            success = false;
        }
        return success;
    }

    public boolean checkCardNum(String account) {
        boolean success = true;
        Integer limit = GlobalCode._CARD_LIMIT_NUM.getCode();
        List<BankCardDto> cards = this.getBankcards(account);
        Integer num = !CollectionUtils.isEmpty(cards) ? cards.size() : 0;
        if (num > limit) {
            success = false;
        }
        return success;
    }

    public Boolean checkBindCards(String account) {
        Boolean success = true;
        List<BankCardDto> cards = this.getBankcards(account);
        if (CollectionUtils.isEmpty(cards)) {
            success = false;
        }
        return success;
    }

    @CacheEvict(key = "#root.target.getFormatKey(#p0)",
            cacheManager = CacheConfig.CacheManagerNames.REDIS_CACHE_MANAGER,
            cacheNames = CacheConfig.EhRedisCacheNames.CACHE_60MINS)
    public void bindBankcard(String account, MemberBankCard bankcard, String ip) {
        if (null == bankcard || StringUtils.isEmpty(account)) {
            return;
        }
        String channelCode = StringUtils.isEmpty(bankcard.getChannelCode())? bankcard.getBankName() : bankcard.getChannelCode();
        DrawChannel drawChannel = drawChannelManager.findByChannelCode(channelCode);
        Timestamp createTime = new Timestamp(System.currentTimeMillis());
        Integer okStatus = GlobalCode._ENABLE.getCode();
        String bankName = null != drawChannel ? drawChannel.getChannelName() : bankcard.getBankName();
        bankcard.setCreateTime(createTime);
        bankcard.setOkStatus(okStatus);
        bankcard.setBankName(bankName);
        bankcard.setAccount(account);
        bankCardDao.save(bankcard);
        String logType = LogTypeEnum._C_BANKCARD.getType();
        memberLogService.saveEventLog(account, logType, null, bankcard.getBankAccount(), IPUtils.ipToLong(ip));
    }

    @Cacheable(key = "#root.target.getFormatKey(#p0)",
            cacheManager = CacheConfig.CacheManagerNames.REDIS_CACHE_MANAGER,
            cacheNames = CacheConfig.EhRedisCacheNames.CACHE_60MINS)
    public List<BankCardDto> getBankcards(String account) {
        Integer okStatus = GlobalCode._ENABLE.getCode();
        List<MemberBankCard> list = bankCardDao.findByAccountAndOkStatus(account, okStatus);
        List<BankCardDto> dtos = this.assembleData(list);
        return dtos;
    }

    public List<BankCardDto> assembleData(List<MemberBankCard> list) {
        List<BankCardDto> dtos = Collections.emptyList();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        dtos = new ArrayList<BankCardDto>();
        Iterator<MemberBankCard> iterable = list.iterator();
        BankCardDto dto = null;
        DrawChannel drawChannel = null;
        while (iterable.hasNext()) {
            MemberBankCard card = iterable.next();
            dto = new BankCardDto();
            String bankAccount = card.getBankAccount();
            String channelCode = card.getChannelCode();
            drawChannel = drawChannelManager.findByChannelCode(channelCode);
            String bankName = null != drawChannel ? drawChannel.getChannelName() : card.getBankName();
            String icon = null != drawChannel ? drawChannel.getIcon() : "";
            dto.setBankAccount(bankAccount);
            dto.setBankName(bankName);
            dto.setBankRealName(card.getBankRealName());
            dto.setBankType(card.getBankType());
            dto.setIcon(icon);
            dtos.add(dto);
        }
        return dtos;
    }

    public String getFormatKey(String account) {
        String key = String.format(RedisCacheKey.MEMBER_CARD_KEY, account);
        return key;
    }
}