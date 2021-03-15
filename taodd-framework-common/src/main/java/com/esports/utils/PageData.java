package com.esports.utils;

import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class PageData implements Serializable {

    private static final long serialVersionUID = 1L;
    private int totalPages;
    private long totalElements;
    private int pageSize;
    private int pageNumber;
    private List<?> contents;

    public static Integer getPagOrDefault(Integer page){
        page = (null == page || 0 > page) ? 0 : page;
        return page;
    }

    public static Integer getPageSizeOrDefault(Integer pageSize){
        pageSize = (null == pageSize || 0 > pageSize) ? 10 : pageSize;
        return pageSize;
    }

    public static PageData builder(Page<?> page) {
        Assert.notNull(page, "page can't be null");
        PageData pageData = new PageData();
        pageData.setContents(page.getContent());
        pageData.setPageSize(page.getSize());
        pageData.setTotalElements(page.getTotalElements());
        pageData.setTotalPages(page.getTotalPages());
        pageData.setPageNumber(page.getNumber());
        return pageData;
    }

    public static PageData builder(List<?> page) {
        Assert.notNull(page, "page can't be null");
        PageData pageData = new PageData();
        pageData.setContents(page);
        pageData.setPageSize(page.size());
        pageData.setTotalElements(page.size());
        pageData.setTotalPages(1);
        pageData.setPageNumber(1);
        return pageData;
    }

    public static PageData builder(List<?> data, Integer pageSize, long totalElements, Integer totalPages, Integer pageNumber) {
        PageData pageData = new PageData();
        data = CollectionUtils.isEmpty(data) ? Collections.emptyList() : data;
        pageData.setContents(data);
        pageData.setPageSize(pageSize);
        pageData.setTotalElements(totalElements);
        pageData.setTotalPages(totalPages);
        pageData.setPageNumber(pageNumber);
        return pageData;
    }

    public static PageData startPage(List<?> list, long totalElements, Integer pageNo, Integer pageSize) {
        if (CollectionUtils.isEmpty(list)) {
            return PageData.builder(null, pageSize, totalElements, 1, pageNo);
        }
        //下标从0开始
        pageNo = pageNo + 1;
        Integer count = list.size(); // 记录总数
        Integer pageCount = 0; // 页数
        if (count % pageSize == 0) {
            pageCount = count / pageSize;
        } else {
            pageCount = count / pageSize + 1;
        }
        int fromIndex = 0; // 开始索引
        int toIndex = 0; // 结束索引
        if (pageNo > pageCount) {
            pageNo = pageCount;
        }
        if (pageNo != pageCount) {
            fromIndex = (pageNo - 1) * pageSize;
            toIndex = fromIndex + pageSize;
        } else {
            fromIndex = (pageNo - 1) * pageSize;
            toIndex = count;
        }
        if (toIndex >= count) {
            toIndex = count;
        }
        totalElements = totalElements <=0? list.size() : totalElements;
        List<?> pageList = list.subList(fromIndex, toIndex);
        PageData pageData = PageData.builder(pageList, pageSize, totalElements, pageCount, pageNo);
        return pageData;
    }


    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public List<?> getContents() {
        return contents;
    }

    public void setContents(List<?> contents) {
        this.contents = contents;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
