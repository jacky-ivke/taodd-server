package com.esports.center.scheme.service;

import com.esports.center.basic.dto.DrawScopeDto;
import com.esports.center.scheme.bean.db1.DrawScheme;
import com.esports.center.scheme.dao.db1.DrawSchemelDao;
import com.esports.center.scheme.dto.DrawSchemeCfgDto;
import com.esports.constant.GlobalCode;
import com.esports.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DrawSchemeManager {


    @Autowired
    private DrawSchemelDao drawSchemelDao;

    /**
     * 判断VIP是否允许提现
     *
     * @param drawSchemCode
     * @param vip
     * @return
     */
    public boolean checkAllowDraw(String drawSchemCode, Integer vip) {
        boolean success = true;
        Integer okStatus = GlobalCode._ENABLE.getCode();
        DrawScheme scheme = drawSchemelDao.findBySchemeCodeAndOkStatus(drawSchemCode, okStatus);
        if (null == scheme) {
            return success;
        }
        String cfg = scheme.getSchemeConfig();
        if (StringUtils.isEmpty(cfg)) {
            return success;
        }
        List<DrawSchemeCfgDto> list = JsonUtil.string2List(cfg, DrawSchemeCfgDto.class);
        if (CollectionUtils.isEmpty(list)) {
            return success;
        }
        for (DrawSchemeCfgDto dto : list) {
            if (!vip.equals(dto.getVip())) {
                continue;
            }
            Boolean allowDraw = dto.getDrawAllow();
            if (!allowDraw.booleanValue()) {
                success = false;
            }
            break;
        }
        return success;
    }

    public boolean checkDrawScope(String drawSchemCode, Integer vip, BigDecimal amount) {
        boolean success = true;
        DrawScopeDto scope = this.getDrawScopLimit(drawSchemCode, vip);
        BigDecimal lowLimit = scope.getMinAmount();
        BigDecimal upLimit = scope.getMaxAmount();
        if (upLimit.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(lowLimit) < 0 || amount.compareTo(upLimit) > 0) {
            success = false;
        }
        return success;
    }

    public DrawScopeDto getDrawScopLimit(String drawSchemCode, Integer vip) {
        DrawScopeDto dto = new DrawScopeDto();
        BigDecimal lowLimit = BigDecimal.ZERO;
        BigDecimal upLimit = BigDecimal.ZERO;
        DrawSchemeCfgDto drawSchemeCfg = this.getDrawScope(drawSchemCode, vip);
        if (null != drawSchemeCfg) {
            String scope = drawSchemeCfg.getDrawScope();
            if (!StringUtils.isEmpty(scope)) {
                lowLimit = null != scope.split("-")[0] ? new BigDecimal(scope.split("-")[0]) : lowLimit;
                upLimit = null != scope.split("-")[1] ? new BigDecimal(scope.split("-")[1]) : upLimit;
            }
        }
        dto.setMinAmount(lowLimit);
        dto.setMaxAmount(upLimit.compareTo(BigDecimal.ZERO) > 0 && upLimit.compareTo(lowLimit) >= 0 ? upLimit : new BigDecimal("10000"));
        return dto;
    }

    public DrawSchemeCfgDto getDrawScope(String drawSchemCode, Integer vip) {
        DrawSchemeCfgDto drawSchemeCfg = null;
        Integer okStatus = GlobalCode._ENABLE.getCode();
        DrawScheme scheme = drawSchemelDao.findBySchemeCodeAndOkStatus(drawSchemCode, okStatus);
        if (null == scheme || StringUtils.isEmpty(scheme.getSchemeConfig())) {
            return drawSchemeCfg;
        }
        String cfg = scheme.getSchemeConfig();
        List<DrawSchemeCfgDto> list = JsonUtil.string2List(cfg, DrawSchemeCfgDto.class);
        if (CollectionUtils.isEmpty(list)) {
            return drawSchemeCfg;
        }
        for (DrawSchemeCfgDto dto : list) {
            if (!vip.equals(dto.getVip())) {
                continue;
            }
            drawSchemeCfg = dto;
            break;
        }
        return drawSchemeCfg;
    }

    public boolean checkDrawTotalOrDrawCount(String drawSchemCode, Integer vip, Integer drawCount, BigDecimal drawTotal) {
        boolean success = true;
        Integer okStatus = GlobalCode._ENABLE.getCode();
        DrawScheme scheme = drawSchemelDao.findBySchemeCodeAndOkStatus(drawSchemCode, okStatus);
        if (null == scheme) {
            return success;
        }
        String cfg = scheme.getSchemeConfig();
        if (StringUtils.isEmpty(cfg)) {
            return success;
        }
        List<DrawSchemeCfgDto> list = JsonUtil.string2List(cfg, DrawSchemeCfgDto.class);
        if (CollectionUtils.isEmpty(list)) {
            return success;
        }
        for (DrawSchemeCfgDto dto : list) {
            if (!vip.equals(dto.getVip())) {
                continue;
            }
            Boolean allowDraw = dto.getDrawAllow();
            Integer limitCount = null != dto.getDrawCount() ? dto.getDrawCount() : 0;
            BigDecimal limitTotal = null != dto.getDrawTotal() ? dto.getDrawTotal() : BigDecimal.ZERO;
            if (allowDraw.booleanValue()) {
                if (limitCount > 0 && drawCount >= limitCount) {
                    success = false;
                } else if (BigDecimal.ZERO.compareTo(limitTotal) < 0 && drawTotal.compareTo(limitTotal) > 0) {
                    success = false;
                }
            }
            break;
        }
        return success;
    }

    public BigDecimal getDrawCharge(String drawSchemCode, Integer vip) {
        BigDecimal charge = BigDecimal.ZERO;
        Integer okStatus = GlobalCode._ENABLE.getCode();
        DrawScheme scheme = drawSchemelDao.findBySchemeCodeAndOkStatus(drawSchemCode, okStatus);
        if (null == scheme) {
            return charge;
        }
        String cfg = scheme.getSchemeConfig();
        if (StringUtils.isEmpty(cfg)) {
            return charge;
        }
        List<DrawSchemeCfgDto> list = JsonUtil.string2List(cfg, DrawSchemeCfgDto.class);
        if (CollectionUtils.isEmpty(list)) {
            return charge;
        }
        for (DrawSchemeCfgDto dto : list) {
            if (!vip.equals(dto.getVip())) {
                continue;
            }
            charge = null != dto.getDrawPoint() ? dto.getDrawPoint() : BigDecimal.ZERO;
            break;
        }
        return charge;
    }

}
