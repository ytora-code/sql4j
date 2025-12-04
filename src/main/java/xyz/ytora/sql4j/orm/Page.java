package xyz.ytora.sql4j.orm;

import java.util.List;

/**
 * 分页
 */
public class Page<T> {
    /**
     * 当前页数
     */
    private Integer pageNo;
    /**
     * 每页尺寸
     */
    private Integer pageSize;
    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 总数据量
     */
    private Long total;

    /**
     * 当前页的所有记录
     */
    private List<T> records;

    public Page() {
        this(1, 10);
    }

    public Page(Integer pageNo, Integer pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public Page<T> setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Page<T> setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Integer getPages() {
        return pages;
    }

    public Page<T> setPages(Integer pages) {
        this.pages = pages;
        return this;
    }

    public Long getTotal() {
        return total;
    }

    public Page<T> setTotal(Long total) {
        this.total = total;
        return this;
    }

    public List<T> getRecords() {
        return records;
    }

    public Page<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }
}
