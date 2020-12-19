package com.polaris.blog.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 时机：
 *      搜索内容添加
 *          文章发表的时候，即state变为1的时候
 *      搜索内容删除
 *          文章删除的时候，包括物理删除和修改状态删除
 *      搜索内容修改
 *          TODD:当阅读量更新的时候
 * @param <T>
 */
public class PageList<T> implements Serializable {
    //当前页码
    private long currentPage;
    //每页数
    private long pageSize;
    //总记录数
    private long totalCount;
    //总页数
    private long totalPage;
    //是否第一页
    private boolean isFirst;
    //是否最后一页
    private boolean isLast;
    //数据
    private List<T> contents;

    public PageList(long currentPage, long pageSize, long totalCount) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
        this.isFirst = currentPage == 1;//是否第一页
        this.isLast = currentPage == totalPage;//是否最后一页
    }

    public long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public List<T> getContents() {
        return contents;
    }

    public void setContents(List<T> contents) {
        this.contents = contents;
    }
}
