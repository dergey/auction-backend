package com.sergey.zhuravlev.auctionserver.common;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class SimplePage implements Pageable  {

    public static final int DEFAULT_PAGE_SIZE = 5;

    private int pageSize;
    private int pageNumber;

    public SimplePage(Integer pageSize, Integer pageNumber) {
        if (pageSize == null)
            this.pageSize = DEFAULT_PAGE_SIZE;
        else
            this.pageSize = pageSize;
        if (pageSize == null)
            this.pageNumber = 0;
        else
            this.pageNumber = pageNumber;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public long getOffset() {
        return pageNumber * pageSize;
    }

    @Override
    public Sort getSort() {
        return null;
    }

    @Override
    public Pageable next() {
        return null;
    }

    @Override
    public Pageable previousOrFirst() {
        return null;
    }

    @Override
    public Pageable first() {
        return null;
    }

    @Override
    public boolean hasPrevious() {
        return pageNumber > 0;
    }
}
