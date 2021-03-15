package com.esports.external.basic.service;


import com.esports.api.center.MemberService;
import com.esports.api.log.MemberLogService;
import com.esports.constant.GlobalCode;
import com.esports.external.basic.bean.db1.ExternalGame;
import com.esports.external.basic.dao.db1.ExternalGameDao;
import com.esports.external.basic.dto.GameDto;
import com.esports.external.handler.AbstractTemplate;
import com.esports.external.handler.ProxyFactory;
import com.esports.external.handler.RespResultDTO;
import com.esports.utils.IPUtils;
import com.esports.utils.PageData;
import com.esports.utils.RandomUtil;
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
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class ExternalGameManager {

    @Autowired
    private ExternalGameDao externalGameDao;

    @Autowired
    private ExternalAppManager externalAppManager;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberLogService memberLogService;

    private final static String MOBILE = "mobile";

    private final static String FLASH = "flash";

    public boolean checkAppStatus(String apiCode) {
        boolean success = externalAppManager.checkAppStatus(apiCode);
        return success;
    }

    public PageData getGameList(String type, String tag, Integer source, String apiCode, String gameName, Integer page, Integer pageSize) {
        Sort.Order[] orders = {Sort.Order.asc("priority"), Sort.Order.desc("id")};
        Sort sort = Sort.by(Arrays.asList(orders));
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Specification<ExternalGame> spec = new Specification<ExternalGame>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<ExternalGame> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 增加筛选条件
                Predicate predicate = cb.conjunction();
                predicate.getExpressions().add(cb.equal(root.get("okStatus").as(Integer.class), GlobalCode._ENABLE.getCode()));
                if (!StringUtils.isEmpty(type)) {
                    predicate.getExpressions().add(cb.equal(root.get("type").as(String.class), type));
                }
                if (!StringUtils.isEmpty(tag)) {
                    predicate.getExpressions().add(cb.equal(root.get("tag").as(String.class), tag));
                }
                String support = GlobalCode._MOBILE.getCode().equals(source) ? MOBILE : FLASH;
                predicate.getExpressions().add(cb.equal(root.get("support").as(String.class), support));
                if (!StringUtils.isEmpty(gameName)) {
                    predicate.getExpressions().add(cb.like(root.get("gameName").as(String.class), "%" + gameName + "%"));
                }
                if (!StringUtils.isEmpty(apiCode)) {
                    predicate.getExpressions().add(cb.like(root.get("apiCode").as(String.class), "%" + apiCode + "%"));
                }
                return predicate;
            }
        };
        Page<ExternalGame> pages = externalGameDao.findAll(spec, pageable);
        List<GameDto> list = this.assembleData(pages);
        PageData pageData = PageData.builder(pages);
        pageData.setContents(list);
        return pageData;
    }

    private List<GameDto> assembleData(Page<ExternalGame> pages) {
        List<GameDto> dtos = new ArrayList<GameDto>();
        List<ExternalGame> list = pages.getContent();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        Iterator<ExternalGame> iterator = list.iterator();
        GameDto dto = null;
        while (iterator.hasNext()) {
            ExternalGame game = iterator.next();
            dto = new GameDto();
            dto.setApiCode(game.getApiCode());
            dto.setGameCode(game.getGameCode());
            dto.setGameName(game.getGameName());
            dto.setGameIcon(game.getGameIcon());
            dto.setGameCover(game.getGameCover());
            dto.setType(game.getType());
            dtos.add(dto);
        }
        return dtos;
    }

    public String getGameUrl(String account, String apiCode, String gameCode, String gameType, Integer deviceType, Integer lang, String ip) {
        externalAppManager.oneKeyRecovery(account, deviceType, lang, ip);
        BigDecimal balance = memberService.getBalance(account);
        String transferNo = RandomUtil.getUUID("");
        Long timestamp = System.currentTimeMillis();
        AbstractTemplate template = ProxyFactory.getProxy(apiCode);
        RespResultDTO dto = template.playGame(account, deviceType, ip, lang, gameCode, gameType, balance, transferNo, timestamp);
        String url = null != dto && null != dto.getSuccess() && null != dto.getData() ? dto.getData().toString() : "";
        externalAppManager.checkTransferStatus(apiCode, account, transferNo, balance, ip, lang, timestamp);
        memberLogService.savePlayGameLog(account, apiCode, gameCode, IPUtils.ipToLong(ip));
        return url;
    }

    public String getGameZhName(String apiCode, String gameCode) {
        String gameName = "";
        ExternalGame game = externalGameDao.findByApiCodeAndGameCode(apiCode, gameCode);
        if (null == game) {
            return gameName;
        }
        gameName = game.getGameName();
        return gameName;
    }
}
