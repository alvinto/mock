package com.pinganfu.mockall.web.controller;

import java.util.List;

/**
 * Created by WANGQIAODONG581 on 2016-06-14.
 */
public class PageData<T> {

    List<T> data;
    
    List<Integer> pageList;
    /**
     * 总页数
     */
    int totalPages;
    /**
     * 每页显示条数
     */
    int pageSize;
    /**
     * 当前页数
     */
    int pageNum;
    /**
     * 总记录数
     */
    int totalCnt;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
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

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getTotalCnt() {
        return totalCnt;
    }

    public void setTotalCnt(int totalCnt) {
        this.totalCnt = totalCnt;
    }

	public List<Integer> getPageList() {
		return pageList;
	}

	public void setPageList(List<Integer> pageList) {
		this.pageList = pageList;
	}

}
