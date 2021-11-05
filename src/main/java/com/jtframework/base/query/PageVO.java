package com.jtframework.base.query;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2017/12/21
 */
@Data
public final class PageVO<T> implements Serializable {
    private static final long serialVersionUID = -4106030982324955419L;
    private long start;
    private long pageSize;
    private List<T> data;
    private long totalCount;
    private long totalPageCount;
    private long currentPageNo;
    private boolean hasNextPage;
    private boolean hasPreviousPage;
    private boolean isStartPage;
    private boolean isEndPage;

    public PageVO() {
        this(0, 0, 20, new ArrayList());
    }

    public PageVO(long start, long totalSize, long pageSize, List<T> data) {
        this.pageSize = 20;
        this.data = new ArrayList(0);
        this.pageSize = pageSize;
        this.start = start;
        this.totalCount = totalSize;
        this.data = data;
        this.init();
    }

    protected void init() {
        if (this.totalCount % this.pageSize == 0) {
            this.totalPageCount = this.totalCount / this.pageSize;
        } else {
            this.totalPageCount = this.totalCount / this.pageSize + 1;
        }

        this.currentPageNo = this.start / this.pageSize + 1;
        this.hasNextPage = this.currentPageNo < this.totalPageCount;
        this.hasPreviousPage = this.currentPageNo > 1;
        this.isStartPage = this.currentPageNo == 1;
        this.isEndPage = this.currentPageNo == this.totalPageCount;
    }

    public static int getStartOfPage(int pageNo) {
        return getStartOfPage(pageNo, 20);
    }

    public static int getStartOfPage(int pageNo, int pageSize) {
        return (pageNo - 1) * pageSize;
    }
}
