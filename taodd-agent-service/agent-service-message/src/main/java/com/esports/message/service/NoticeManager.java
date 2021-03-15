package com.esports.message.service;


import com.esports.constant.GlobalCode;
import com.esports.constant.MessageCode;
import com.esports.constant.PlayerCode;
import com.esports.message.bean.db1.MessageText;
import com.esports.message.bean.db1.Notice;
import com.esports.message.dao.db1.NoticeDao;
import com.esports.message.dto.NoticeDto;
import com.esports.message.dto.UnReadMsgDto;
import com.esports.processor.ApiGateway;
import com.esports.utils.DateUtils;
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
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Slf4j
@Service
public class NoticeManager {

    @Autowired
    private NoticeDao noticeDao;

    @Autowired
    private SysMessgeManager sysMessgeManager;

    @Autowired
    private MessageManager messageManager;

    private static final Integer MSG_ATTR_POP = 0;

    private static final Integer MSG_ATTR_ROLL = 1;

    public MessageText getMessageText(String msgType, String identity, Integer source) {
        MessageText msgText = messageManager.getMessageText(msgType, identity, source);
        return msgText;
    }

    private String getMessageContent(String msgType, String template){

        return "";
    }


    private void sendEventMessge(String msgType, String account, String agent, String identity, Integer source, BigDecimal amount) {
        MessageText messageText = this.getMessageText(msgType, identity, source);
        if (null == messageText) {
            return;
        }
        String title = messageText.getTitle();
        String text = messageText.getContent();
        String msgPlatforms = String.valueOf(source);
        if (StringUtils.isEmpty(text)) {
            return;
        }
        //格式化消息内容
        String tradeData = DateUtils.getCurrentTime();
        text = String.format(text, agent, tradeData, amount);
        this.saveEventMessage(title, text, account, identity, msgPlatforms);
    }

    public void saveEventMessage(final String title, final String messageText, final String receve, final String identity, final String platform) {
        Integer type = MessageCode._EVENT.getCode();
        Notice notice = new Notice();
        notice.setType(type);
        notice.setAccounts(receve);
        notice.setRoll(Boolean.FALSE);
        notice.setPop(Boolean.FALSE);
        notice.setTitle(title);
        notice.setContent(messageText);
        notice.setIdentity(identity);
        notice.setPlatform(platform);
        notice.setPriority(0);
        notice.setStartTime(Timestamp.valueOf(DateUtils.getCurrentTime()));
        notice.setEndTime(Timestamp.valueOf(DateUtils.getAfterThirtyDaysTime()));
        notice.setAuthor(GlobalCode._SYS_ACTION.getMessage());
        notice.setOperator(GlobalCode._SYS_ACTION.getMessage());
        notice.setCreateTime(new Timestamp(System.currentTimeMillis()));
        noticeDao.save(notice);
    }

    protected List<NoticeDto> sort(List<NoticeDto> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        Collections.sort(list, new Comparator<NoticeDto>() {
            @Override
            public int compare(NoticeDto o1, NoticeDto o2) {
                Timestamp t1 = o1.getCreateTime();
                Timestamp t2 = o2.getCreateTime();
                return t1.before(t2) ? 1 : -1;
            }
        });
        return list;
    }

    public PageData getNotices(Integer source, String identity, String account, Integer page, Integer pageSize, String startTime, String endTime) {
        List<Notice> list = new ArrayList<Notice>();
        List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
        //默认时间查询最近30天
        startTime = StringUtils.isEmpty(startTime) ? DateUtils.getBeforeThirtyDaysTime() : startTime;
        endTime = StringUtils.isEmpty(endTime) ? DateUtils.getCurrentTime() : endTime;
        FutureTask<Integer> t1 = this.getGroupMessage(list, source, identity, startTime, endTime);
        FutureTask<Integer> t2 = this.getAccountsMessage(list, source, identity, account, startTime, endTime);
        FutureTask<Integer> t3 = this.getEventMessage(list, identity, account, startTime, endTime);
        tasks.add(t1);
        tasks.add(t2);
        tasks.add(t3);
        ApiGateway.syncwait(tasks);
        List<NoticeDto> dtos = this.assembleData(list, account);
        dtos = this.sort(dtos);
        PageData pageData = PageData.startPage(dtos, 0, page, pageSize);
        return pageData;
    }

    public FutureTask<Integer> getGroupMessage(final List<Notice> list, final Integer source, final String identity, final String startTime, final String endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                List<Notice> datas = getGroupMessage(source, identity, startTime, endTime);
                if (!CollectionUtils.isEmpty(datas)) {
                    synchronized (list) {
                        list.addAll(datas);
                    }
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getAccountsMessage(final List<Notice> list, final Integer source, final String identity, final String account, final String startTime, final String endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                List<Notice> datas = getAccountsMessage(source, identity, account, startTime, endTime);
                if (!CollectionUtils.isEmpty(datas)) {
                    synchronized (list) {
                        list.addAll(datas);
                    }
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getEventMessage(final List<Notice> list, final String identity, final String account, final String startTime, final String endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                List<Notice> datas = getEventMessage(identity, account, startTime, endTime);
                if (!CollectionUtils.isEmpty(datas)) {
                    synchronized (list) {
                        list.addAll(datas);
                    }
                }
                return 1;
            }
        });
        return futureTask;
    }

    public List<Notice> getGroupMessage(Integer source, String identity, String startTime, String endTime) {
        Sort.Order[] orders = {Sort.Order.desc("createTime"), Sort.Order.asc("priority")};
        Sort sort = Sort.by(Arrays.asList(orders));
        Specification<Notice> spec = new Specification<Notice>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Notice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 增加筛选条件
                Timestamp now = new Timestamp(System.currentTimeMillis());
                Predicate predicate = cb.conjunction();
                predicate.getExpressions().add(cb.equal(root.get("type").as(Integer.class), MessageCode._GROUP.getCode()));
                predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("startTime").as(Timestamp.class), now));
                predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("endTime").as(Timestamp.class), now));
                if (!StringUtils.isEmpty(identity)) {
                    predicate.getExpressions().add(cb.like(root.get("identity").as(String.class), "%" + identity + "%"));
                }
                if (null != source) {
                    predicate.getExpressions().add(cb.like(root.get("platform").as(String.class), "%" + source + "%"));
                }
                if (!StringUtils.isEmpty(startTime)) {
                    Timestamp start = DateUtils.getDayStartTime(DateUtils.stringFormatDate(startTime));
                    predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("startTime").as(Timestamp.class), start));
                }
                if (!StringUtils.isEmpty(endTime)) {
                    Timestamp end = DateUtils.getDayEndTime(DateUtils.stringFormatDate(endTime));
                    predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("endTime").as(Timestamp.class), end));
                }
                return predicate;
            }
        };
        List<Notice> list = noticeDao.findAll(spec, sort);
        return list;
    }

    public List<Notice> getAccountsMessage(Integer source, String identity, String account, String startTime, String endTime) {
        Sort.Order[] orders = {Sort.Order.desc("createTime"), Sort.Order.asc("priority")};
        Sort sort = Sort.by(Arrays.asList(orders));
        Specification<Notice> spec = new Specification<Notice>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Notice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 增加筛选条件
                Timestamp now = new Timestamp(System.currentTimeMillis());
                Predicate predicate = cb.conjunction();
                predicate.getExpressions().add(cb.equal(root.get("type").as(String.class), MessageCode._MEMBER.getCode()));
                predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("startTime").as(Timestamp.class), now));
                predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("endTime").as(Timestamp.class), now));
                if (!StringUtils.isEmpty(account)) {
                    predicate.getExpressions().add(cb.like(root.get("accounts").as(String.class), "%" + account + "%"));
                }
                if (!StringUtils.isEmpty(identity)) {
                    predicate.getExpressions().add(cb.like(root.get("identity").as(String.class), "%" + identity + "%"));
                }
                if (null != source) {
                    predicate.getExpressions().add(cb.like(root.get("platform").as(String.class), "%" + source + "%"));
                }
                if (!StringUtils.isEmpty(startTime)) {
                    Timestamp start = DateUtils.getDayStartTime(DateUtils.stringFormatDate(startTime));
                    predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("startTime").as(Timestamp.class), start));
                }
                if (!StringUtils.isEmpty(endTime)) {
                    Timestamp end = DateUtils.getDayEndTime(DateUtils.stringFormatDate(endTime));
                    predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("endTime").as(Timestamp.class), end));
                }
                return predicate;
            }
        };
        List<Notice> list = noticeDao.findAll(spec, sort);
        return list;
    }

    public List<Notice> getEventMessage(String identity, String account, String startTime, String endTime) {
        Sort.Order[] orders = {Sort.Order.desc("createTime"), Sort.Order.asc("priority")};
        Sort sort = Sort.by(Arrays.asList(orders));
        Specification<Notice> spec = new Specification<Notice>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Notice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 增加筛选条件
                Timestamp now = new Timestamp(System.currentTimeMillis());
                Predicate predicate = cb.conjunction();
                predicate.getExpressions().add(cb.equal(root.get("type").as(Integer.class), MessageCode._EVENT.getCode()));
                predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("startTime").as(Timestamp.class), now));
                predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("endTime").as(Timestamp.class), now));
                if (!StringUtils.isEmpty(account)) {
                    predicate.getExpressions().add(cb.equal(root.get("accounts").as(String.class), account));
                }
                if (!StringUtils.isEmpty(identity)) {
                    predicate.getExpressions().add(cb.like(root.get("identity").as(String.class), "%" + identity + "%"));
                }
                if (!StringUtils.isEmpty(startTime)) {
                    Timestamp start = DateUtils.getDayStartTime(DateUtils.stringFormatDate(startTime));
                    predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("startTime").as(Timestamp.class), start));
                }
                if (!StringUtils.isEmpty(endTime)) {
                    Timestamp end = DateUtils.getDayEndTime(DateUtils.stringFormatDate(endTime));
                    predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("endTime").as(Timestamp.class), end));
                }
                return predicate;
            }
        };
        List<Notice> list = noticeDao.findAll(spec, sort);
        return list;
    }

    private List<NoticeDto> assembleData(List<Notice> list, String account) {
        List<NoticeDto> dtos = new ArrayList<NoticeDto>();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        NoticeDto dto = null;
        Iterator<Notice> iterator = list.iterator();
        while (iterator.hasNext()) {
            Notice notice = iterator.next();
            Long messageId = notice.getId();
            Integer messageStatus = sysMessgeManager.checkMessageStatus(messageId, account);
            if (MessageCode._STATUS_DELETE.getCode().equals(messageStatus)) {
                continue;
            }
            Integer readStatus = MessageCode._STATUS_READ.getCode();
            dto = new NoticeDto();
            dto.setStatus(readStatus.equals(messageStatus) ? 1 : 0);
            dto.setId(notice.getId());
            dto.setTitle(notice.getTitle());
            dto.setCreateTime(notice.getCreateTime());
            dto.setAuthor(notice.getAuthor());
            dtos.add(dto);
        }
        return dtos;
    }

    public String getNoticesDetail(Long id) {
        Notice notice = noticeDao.getOne(id);
        if (null == notice) {
            return null;
        }
        String content = notice.getContent();
        return content;
    }

    public PageData getAdviseMsg(Integer source, Integer type, String account, Integer page, Integer pageSize, String startTime, String endTime) {
        Sort.Order[] orders = {Sort.Order.desc("id"), Sort.Order.desc("createTime")};
        Sort sort = Sort.by(Arrays.asList(orders));
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Specification<Notice> spec = new Specification<Notice>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Notice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                //查询发布中的公告信息
                Timestamp now = new Timestamp(System.currentTimeMillis());
                Predicate predicate = cb.conjunction();
                predicate.getExpressions().add(cb.equal(root.get("type").as(Integer.class), MessageCode._NOTICE.getCode()));
                predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("startTime").as(Timestamp.class), now));
                predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("endTime").as(Timestamp.class), now));
                if (null != source) {
                    predicate.getExpressions().add(cb.like(root.get("platform").as(String.class), "%" + source + "%"));
                }
                if (null != type && MSG_ATTR_ROLL.equals(type)) {
                    predicate.getExpressions().add(cb.equal(root.get("roll").as(Boolean.class), Boolean.TRUE));
                } else if (null != type && MSG_ATTR_POP.equals(type)) {
                    predicate.getExpressions().add(cb.equal(root.get("pop").as(Boolean.class), Boolean.TRUE));
                }
                if (!StringUtils.isEmpty(startTime)) {
                    Timestamp start = DateUtils.getDayStartTime(DateUtils.stringFormatDate(startTime));
                    predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("startTime").as(Timestamp.class), start));
                }
                if (!StringUtils.isEmpty(endTime)) {
                    Timestamp end = DateUtils.getDayEndTime(DateUtils.stringFormatDate(endTime));
                    predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("endTime").as(Timestamp.class), end));
                }
                return predicate;
            }
        };
        Page<Notice> pages = noticeDao.findAll(spec, pageable);
        List<Notice> list = pages.getContent();
        List<NoticeDto> dtos = this.assembleData(list, account);
        PageData pageData = PageData.builder(pages);
        pageData.setContents(dtos);
        return pageData;
    }

    public void sendMessage(Long messageId, String account) {
        sysMessgeManager.sendMessage(messageId, account);
    }

    public void deleteMessage(Long[] ids, String account) {
        sysMessgeManager.deleteMessage(ids, account);
    }

    public void readMessage(Long[] ids, String account) {
        if (null == ids || ids.length < 1) {
            return;
        }
        for (Long id : ids) {
            this.sendMessage(id, account);
        }
    }

    public PageData getMessage(Integer source, String account, Integer pageNo, Integer pageSize, String startTime, String endTime) {
        String identity = PlayerCode._AGENT.getCode();
        PageData data = this.getNotices(source, identity, account, pageNo, pageSize, startTime, endTime);
        return data;
    }

    public String getNoticesDetail(Long id, String account) {
        String content = this.getNoticesDetail(id);
        this.sendMessage(id, account);
        return content;
    }

    public UnReadMsgDto getUnreadMsg(Integer source, String account, String identity) {
        UnReadMsgDto dto = new UnReadMsgDto();
        source = null == source ? GlobalCode._PC.getCode() : source;
        identity = StringUtils.isEmpty(identity) ? PlayerCode._MEMBER.getCode() : identity;
        Timestamp startTime = DateUtils.getDayStartTime(DateUtils.stringFormatDate(DateUtils.getBeforeThirtyDaysTime()));
        Timestamp endTime = DateUtils.getDayEndTime(DateUtils.stringFormatDate(DateUtils.getCurrentTime()));
        Integer unReadNoticeMsg = noticeDao.getUnReadNoticeMsg(account, identity, source, startTime, endTime);
        Integer unReadAdviseMsg = noticeDao.getUnReadAdviseMsg(account, source, startTime, endTime);
        dto.setUnReadNoticeMsg(unReadNoticeMsg);
        dto.setUnReadAdviseMsg(unReadAdviseMsg);
        return dto;
    }
}
