package com.wanding.xingpos.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 交易明细返回实体
 */
public class OrderListData implements Serializable {
	
	private String message;//": "查询成功",
	private String status;//": 200
	
	private Integer countRow;//": 7,
	private Integer numPerPage;//": 1,
	private Integer totalCount;//": 7,
	private Integer pageNum;//": 1
	

	private List<OrderDetailData> orderList;
	
	public OrderListData() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getCountRow() {
		return countRow;
	}

	public void setCountRow(Integer countRow) {
		this.countRow = countRow;
	}

	public Integer getNumPerPage() {
		return numPerPage;
	}

	public void setNumPerPage(Integer numPerPage) {
		this.numPerPage = numPerPage;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public List<OrderDetailData> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<OrderDetailData> orderList) {
		this.orderList = orderList;
	}

	

	
}
